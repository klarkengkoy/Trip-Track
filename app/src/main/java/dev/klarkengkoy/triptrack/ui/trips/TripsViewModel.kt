package dev.klarkengkoy.triptrack.ui.trips

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.model.Category
import dev.klarkengkoy.triptrack.model.PaymentMethod
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.TransactionType
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

data class TripsUiState(
    // The trip currently being viewed by the user. Null if viewing the list of all trips.
    val selectedTrip: Trip? = null,
    val trips: List<Trip> = emptyList(),
    // A map of all transactions, keyed by the ID of the trip they belong to.
    val transactionsByTrip: Map<String, List<Transaction>> = emptyMap()
) {
    // Computed property to get the transactions for the currently selected trip.
    val selectedTripTransactions: List<Transaction>
        get() = selectedTrip?.let { transactionsByTrip[it.id] }.orEmpty()
}

@HiltViewModel
class TripsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * Sets the currently selected trip to null, causing the UI to go back to the list view.
     */
    fun unselectTrip() {
        _uiState.update { it.copy(selectedTrip = null) }
    }

    /**
     * Sets the provided trip as the currently selected one for detailed viewing.
     */
    fun selectTrip(trip: Trip) {
        _uiState.update { it.copy(selectedTrip = trip) }
    }

    private fun loadMockData() {
        // Mock Categories and Payment Methods
        val foodCategory = Category(name = "Food", iconRes = R.mipmap.ic_launcher)
        val transportCategory = Category(name = "Transport", iconRes = R.mipmap.ic_launcher)
        val accommodationCategory = Category(name = "Accommodation", iconRes = R.mipmap.ic_launcher)

        val creditCard = PaymentMethod(name = "Credit Card", iconRes = R.mipmap.ic_launcher)
        val cash = PaymentMethod(name = "Cash", iconRes = R.mipmap.ic_launcher)

        // Mock Trips
        val tokyoTrip = Trip(
            name = "Tokyo Adventure",
            currency = "JPY",
            startDate = LocalDate.now().minusDays(2),
            totalBudget = 200000.0,
            // The 'isActive' flag is no longer used to determine the selected trip.
            // A trip is considered "active" in the UI simply by being in the 'selectedTrip' state.
            isActive = true
        )
        val seoulTrip = Trip(
            name = "Seoul Searching",
            currency = "KRW",
            startDate = LocalDate.of(2023, 10, 10),
            endDate = LocalDate.of(2023, 10, 17),
            totalBudget = 1500000.0
        )
        val baliTrip = Trip(
            name = "Bali Escape",
            currency = "IDR",
            startDate = LocalDate.of(2023, 5, 20),
            endDate = LocalDate.of(2023, 5, 28),
            totalBudget = 10000000.0
        )

        // Mock Transactions for all trips
        val tokyoTransactions = listOf(
            Transaction(
                tripId = tokyoTrip.id,
                notes = "Train from Narita Airport",
                amount = 3200.0,
                date = LocalDate.now().minusDays(2),
                category = transportCategory,
                paymentMethod = creditCard,
                type = TransactionType.EXPENSE
            ),
            Transaction(
                tripId = tokyoTrip.id,
                notes = "Ramen at Ichiran",
                amount = 1500.0,
                date = LocalDate.now().minusDays(2),
                category = foodCategory,
                paymentMethod = cash,
                type = TransactionType.EXPENSE
            ),
            Transaction(
                tripId = tokyoTrip.id,
                notes = "Got paid for a freelance gig",
                amount = 25000.0,
                date = LocalDate.now().minusDays(1),
                category = foodCategory, // Placeholder
                paymentMethod = cash,
                type = TransactionType.INCOME
            )
        )

        val seoulTransactions = listOf(
            Transaction(
                tripId = seoulTrip.id,
                notes = "Kimchi jjigae",
                amount = 12000.0,
                date = LocalDate.of(2023, 10, 11),
                category = foodCategory,
                paymentMethod = creditCard,
                type = TransactionType.EXPENSE
            )
        )

        val baliTransactions = listOf(
            Transaction(
                tripId = baliTrip.id,
                notes = "Surfing lessons",
                amount = 500000.0,
                date = LocalDate.of(2023, 5, 22),
                category = accommodationCategory, // Placeholder
                paymentMethod = cash,
                type = TransactionType.EXPENSE
            )
        )

        _uiState.value = TripsUiState(
            // For now, we'll keep the Tokyo trip selected by default to maintain the UI state.
            // In a real app, this would likely be null initially.
            selectedTrip = tokyoTrip,
            trips = listOf(tokyoTrip, seoulTrip, baliTrip),
            transactionsByTrip = mapOf(
                tokyoTrip.id to tokyoTransactions,
                seoulTrip.id to seoulTransactions,
                baliTrip.id to baliTransactions
            )
        )
    }
}
