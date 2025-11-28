package dev.klarkengkoy.triptrack.ui.trips.transaction

import android.net.Uri
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.net.toUri
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

    fun load(transactionId: String?, tripId: String?, category: String?) {
        viewModelScope.launch {
            if (transactionId != null) {
                loadExistingTransaction(transactionId)
            } else {
                initializeNewTransaction(tripId, category)
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
        val activeTrip = tripsRepository.getActiveTrip().first()
        if (activeTrip != null) {
             val transactions = tripsRepository.getTransactions(activeTrip.id).first()
             val transaction = transactions.find { it.id == transactionId }
             if (transaction != null) {
                 populateState(transaction, activeTrip.currency, activeTrip.isCurrencyCustom)
                 return
             }
        }
        
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

    private suspend fun initializeNewTransaction(tripIdArg: String?, categoryArg: String?) {
        val tripId = tripIdArg ?: savedStateHandle.get<String>("tripId") ?: ""
        val categoryRoute = categoryArg ?: savedStateHandle.get<String>("category")

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
