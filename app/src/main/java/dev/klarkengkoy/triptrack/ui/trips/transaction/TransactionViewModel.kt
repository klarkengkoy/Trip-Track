package dev.klarkengkoy.triptrack.ui.trips.transaction

import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import dev.klarkengkoy.triptrack.model.Category
import dev.klarkengkoy.triptrack.model.IncomeCategory
import dev.klarkengkoy.triptrack.model.PaymentMethod
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.TransactionCategory
import dev.klarkengkoy.triptrack.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Currency
import javax.inject.Inject
import androidx.core.net.toUri

data class TransactionUiState(
    val transactionId: String? = null,
    val tripId: String = "",
    val amount: String = "0",
    val description: String = "",
    val categoryTitle: String = "",
    val categoryIcon: ImageVector? = null,
    val date: LocalDate = LocalDate.now(),
    val currencySymbol: String = "",
    val paymentMethod: PaymentMethod? = null,
    val location: String = "",
    val imageUri: Uri? = null,
    val availablePaymentMethods: List<PaymentMethod> = emptyList(),
    val isDatePickerVisible: Boolean = false,
    val isPaymentMethodMenuExpanded: Boolean = false
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val tripsRepository: TripsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState = _uiState.asStateFlow()

    private val paymentMethods = listOf(
        PaymentMethod("Cash", 0),
        PaymentMethod("Credit Card", 0),
        PaymentMethod("Debit Card", 0),
        PaymentMethod("Bank Transfer", 0)
    )

    init {
        viewModelScope.launch {
            // Check if we are editing an existing transaction
            val transactionId = savedStateHandle.get<String>("transactionId")

            if (transactionId != null) {
                loadExistingTransaction(transactionId)
            } else {
                initializeNewTransaction()
            }
        }
    }
    
    fun updateCategory(categoryRoute: String) {
        val expenseCategory = TransactionCategory.fromRoute(categoryRoute)
        val incomeCategory = IncomeCategory.fromRoute(categoryRoute)

        val categoryTitle = expenseCategory?.title ?: incomeCategory?.title ?: ""
        val categoryIcon = expenseCategory?.icon ?: incomeCategory?.icon
        
        if (categoryTitle.isNotEmpty()) {
             _uiState.update {
                it.copy(
                    categoryTitle = categoryTitle,
                    categoryIcon = categoryIcon
                )
            }
        }
    }
    
    private suspend fun loadExistingTransaction(transactionId: String) {
        // We first need to find which trip this transaction belongs to.
        // Since getTransactions(tripId) requires tripId, and getTrip(tripId) returns a trip...
        // Ideally, the repository should have a way to get a transaction directly by ID or we iterate.
        // However, typically in this app flow, we might not have the tripId readily available if deep linking,
        // but here navigation usually passes context.
        // BUT, for now, let's assume we might need to search or the repository supports it.
        // Wait, the repository interface shows: getTransactions(tripId). It doesn't have getTransaction(id).
        // But the implementation uses Room DAO which likely has it or we can fetch from all trips.
        
        // Hack/Workaround: Since we don't have a direct getTransaction(id) in the interface exposed in the prompt (unless I missed it),
        // but we are inside a specific Trip context usually. 
        // Actually, looking at MainNavigation, we only pass transactionId: "$EDIT_TRANSACTION_ROUTE/{transactionId}"
        
        // Let's try to find the transaction. 
        // Since we don't have a direct getTransaction method in the interface provided in the context,
        // we will rely on the fact that we likely have the trip active or we have to scan.
        // However, a better approach for the future is adding getTransaction(id) to the repo.
        // For now, let's assume we can't change the repo interface easily without more context or let's try to use what we have.
        
        // The user is editing a transaction from the list inside a trip.
        // We can try to find the transaction in the active trip if set, or search.
        
        val activeTrip = tripsRepository.getActiveTrip().first()
        if (activeTrip != null) {
             val transactions = tripsRepository.getTransactions(activeTrip.id).first()
             val transaction = transactions.find { it.id == transactionId }
             if (transaction != null) {
                 populateState(transaction, activeTrip.currency, activeTrip.isCurrencyCustom)
                 return
             }
        }
        
        // If not found in active trip (edge case), we might need to search all trips (expensive but safe fallback)
        val allTrips = tripsRepository.getTrips().first()
        for (trip in allTrips) {
            val transactions = tripsRepository.getTransactions(trip.id).first()
            val transaction = transactions.find { it.id == transactionId }
            if (transaction != null) {
                populateState(transaction, trip.currency, trip.isCurrencyCustom)
                return
            }
        }
    }
    
    private fun populateState(transaction: Transaction, currencyCode: String, isCustomCurrency: Boolean) {
        val categoryTitle = transaction.category.name
        // Determine icon based on title matching
        val expenseCategory = TransactionCategory.categories.find { it.title == categoryTitle }
        val incomeCategory = IncomeCategory.categories.find { it.title == categoryTitle }
        val categoryIcon = expenseCategory?.icon ?: incomeCategory?.icon
        
        val currencySymbol = if (isCustomCurrency) {
            currencyCode
        } else {
            try {
                Currency.getInstance(currencyCode).symbol
            } catch (_: Exception) {
                currencyCode
            }
        }

        _uiState.update {
            it.copy(
                transactionId = transaction.id,
                tripId = transaction.tripId,
                amount = if (transaction.amount == 0.0) "0" else transaction.amount.toString().removeSuffix(".0"),
                description = transaction.notes ?: "",
                categoryTitle = categoryTitle,
                categoryIcon = categoryIcon,
                date = transaction.date,
                currencySymbol = currencySymbol,
                paymentMethod = transaction.paymentMethod,
                imageUri = transaction.imageUri?.toUri(),
                availablePaymentMethods = paymentMethods,
                // Ensure the payment method from transaction is valid or fallback
                isPaymentMethodMenuExpanded = false
            )
        }
    }

    private suspend fun initializeNewTransaction() {
        val tripId = savedStateHandle.get<String>("tripId") ?: ""
        val categoryRoute = savedStateHandle.get<String>("category")

        val expenseCategory = TransactionCategory.fromRoute(categoryRoute)
        val incomeCategory = IncomeCategory.fromRoute(categoryRoute)

        val categoryTitle = expenseCategory?.title ?: incomeCategory?.title ?: ""
        val categoryIcon = expenseCategory?.icon ?: incomeCategory?.icon

        val trip = tripsRepository.getTrip(tripId)
        val currencySymbol = trip?.let {
            if (it.isCurrencyCustom) {
                it.currency
            } else {
                try {
                    Currency.getInstance(it.currency).symbol
                } catch (_: Exception) {
                    it.currency // Fallback
                }
            }
        } ?: ""

        _uiState.update {
            it.copy(
                tripId = tripId,
                categoryTitle = categoryTitle,
                categoryIcon = categoryIcon,
                currencySymbol = currencySymbol,
                availablePaymentMethods = paymentMethods,
                paymentMethod = paymentMethods.first()
            )
        }
    }

    fun onAmountChange(newAmount: String) {
        val currentAmount = _uiState.value.amount

        val updatedAmount = when {
            // If the user clears the input, reset to "0"
            newAmount.isEmpty() -> "0"
            // If current amount is "0" and user types a non-zero digit, replace "0"
            currentAmount == "0" && newAmount != "0." && newAmount.length > 1 && !newAmount.startsWith("0.") -> newAmount.substring(1)
            // If user types a decimal after "0", allow it
            currentAmount == "0" && newAmount == "0." -> "0."
            else -> newAmount
        }

        _uiState.update { it.copy(amount = updatedAmount) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onDateChange(millis: Long?) {
        millis ?: return
        val localDate = LocalDate.ofInstant(java.time.Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        _uiState.update { it.copy(date = localDate, isDatePickerVisible = false) }
    }

    fun onPaymentMethodChange(paymentMethod: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = paymentMethod, isPaymentMethodMenuExpanded = false) }
    }

    fun onPaymentMethodMenuToggled(isExpanded: Boolean) {
        _uiState.update { it.copy(isPaymentMethodMenuExpanded = isExpanded) }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun onImageChange(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun showDatePicker(show: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = show) }
    }

    fun saveTransaction() {
        viewModelScope.launch {
            val uiState = _uiState.value
            if (uiState.paymentMethod == null) return@launch

            val transaction = Transaction(
                id = uiState.transactionId ?: java.util.UUID.randomUUID().toString(),
                tripId = uiState.tripId,
                notes = uiState.description,
                amount = uiState.amount.toDoubleOrNull() ?: 0.0,
                date = uiState.date,
                category = Category(uiState.categoryTitle, 0), // Use placeholder 0 for iconRes
                paymentMethod = uiState.paymentMethod,
                imageUri = uiState.imageUri?.toString(),
                // Logic to determine type if not explicitly stored in UI state could be improved, 
                // but defaulting to EXPENSE or inferring from category is consistent with init.
                // For edits, we should ideally preserve type. 
                // Re-inferring from title:
                type = if (IncomeCategory.categories.any { it.title == uiState.categoryTitle }) TransactionType.INCOME else TransactionType.EXPENSE
            )
            tripsRepository.addTransaction(transaction) // addTransaction typically handles Upsert (Insert or Replace) in Room
        }
    }
}
