package dev.klarkengkoy.triptrack.ui.trips.transaction

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Currency
import javax.inject.Inject

data class TransactionUiState(
    val tripId: String = "",
    val amount: String = "",
    val description: String = "",
    val categoryTitle: String = "",
    val categoryIcon: ImageVector? = null,
    val date: LocalDate = LocalDate.now(),
    val currencySymbol: String = "",
    val paymentMethod: PaymentMethod? = null,
    val location: String = "",
    val availablePaymentMethods: List<PaymentMethod> = emptyList(),
    val isDatePickerVisible: Boolean = false
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
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
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
        _uiState.update { it.copy(paymentMethod = paymentMethod) }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(location = location) }
    }

    fun showDatePicker(show: Boolean) {
        _uiState.update { it.copy(isDatePickerVisible = show) }
    }

    fun saveTransaction() {
        viewModelScope.launch {
            val uiState = _uiState.value
            if (uiState.paymentMethod == null) return@launch

            val transaction = Transaction(
                tripId = uiState.tripId,
                notes = uiState.description,
                amount = uiState.amount.toDoubleOrNull() ?: 0.0,
                date = uiState.date,
                category = Category(uiState.categoryTitle, 0), // Use placeholder 0 for iconRes
                paymentMethod = uiState.paymentMethod,
                type = if (TransactionCategory.fromRoute(savedStateHandle.get<String>("category")) != null) TransactionType.EXPENSE else TransactionType.INCOME
            )
            tripsRepository.addTransaction(transaction)
        }
    }
}
