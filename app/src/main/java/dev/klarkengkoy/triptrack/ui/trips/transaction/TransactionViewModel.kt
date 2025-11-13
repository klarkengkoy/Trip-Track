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
import javax.inject.Inject

data class TransactionUiState(
    val tripId: String = "",
    val amount: String = "",
    val description: String = "",
    val categoryTitle: String = "",
    val categoryIcon: ImageVector? = null,
    val date: Long? = null,
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val tripsRepository: TripsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val tripId = savedStateHandle.get<String>("tripId") ?: ""
        val categoryRoute = savedStateHandle.get<String>("category")

        val expenseCategory = TransactionCategory.fromRoute(categoryRoute)
        val incomeCategory = IncomeCategory.fromRoute(categoryRoute)

        val categoryTitle = expenseCategory?.title ?: incomeCategory?.title ?: ""
        val categoryIcon = expenseCategory?.icon ?: incomeCategory?.icon

        _uiState.update {
            it.copy(
                tripId = tripId,
                categoryTitle = categoryTitle,
                categoryIcon = categoryIcon
            )
        }
    }

    fun onAmountChange(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun saveTransaction() {
        viewModelScope.launch {
            val uiState = _uiState.value
            val transaction = Transaction(
                tripId = uiState.tripId,
                notes = uiState.description,
                amount = uiState.amount.toDoubleOrNull() ?: 0.0,
                date = LocalDate.now(),
                category = Category(uiState.categoryTitle, 0),
                paymentMethod = PaymentMethod("Cash", 0),
                type = if (TransactionCategory.fromRoute(savedStateHandle.get<String>("category")) != null) TransactionType.EXPENSE else TransactionType.INCOME
            )
            tripsRepository.addTransaction(transaction)
        }
    }
}
