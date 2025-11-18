package dev.klarkengkoy.triptrack.ui.trips

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private const val TAG = "TripsViewModel"

data class TripsUiState(
    val selectedTrip: Trip? = null,
    val trips: List<Trip> = emptyList(),
    val transactionsByTrip: Map<String, List<Transaction>> = emptyMap(),
    val tripUiState: TripUiState = TripUiState(),
    val selectionMode: Boolean = false,
    val selectedTrips: Set<String> = emptySet()
) {
    val selectedTripTransactions: List<Transaction>
        get() = selectedTrip?.let { transactionsByTrip[it.id] }.orEmpty()
}

data class TripUiState(
    val tripName: String = "",
    val imageUri: String? = null,
    val imageOffsetX: Float = 0f,
    val imageOffsetY: Float = 0f,
    val imageScale: Float = 1f,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val currency: String = "",
    val isCurrencyCustom: Boolean = false,
    val totalBudget: String = "",
    val dailyBudget: String = ""
)

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripsRepository: TripsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        tripsRepository.getTrips()
            .onEach { trips ->
                _uiState.update { it.copy(trips = trips) }

                // Check for a tripId from navigation
                val tripId = savedStateHandle.get<String>("tripId")
                if (tripId != null) {
                    val trip = trips.find { it.id == tripId }
                    if (trip != null) {
                        selectTrip(trip)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onTripClicked(tripId: String) {
        viewModelScope.launch {
            tripsRepository.setActiveTrip(tripId, true)
        }
    }

    fun onTripDeactivated() {
        viewModelScope.launch {
            val activeTrip = tripsRepository.getActiveTrip().first()
            if (activeTrip != null) {
                tripsRepository.setActiveTrip(activeTrip.id, false)
            }
        }
    }

    fun populateTripDetails(tripId: String) {
        viewModelScope.launch {
            val trip = tripsRepository.getTrip(tripId)
            if (trip != null) {
                Log.d(TAG, "Populating details for trip: $trip")
                _uiState.update {
                    it.copy(
                        selectedTrip = trip, // Set the selected trip for context
                        tripUiState = it.tripUiState.copy(
                            tripName = trip.name,
                            imageUri = trip.imageUri,
                            imageOffsetX = trip.imageOffsetX,
                            imageOffsetY = trip.imageOffsetY,
                            imageScale = trip.imageScale,
                            startDate = trip.startDate?.atStartOfDay(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli(),
                            endDate = trip.endDate?.atStartOfDay(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli(),
                            currency = trip.currency,
                            isCurrencyCustom = trip.isCurrencyCustom,
                            totalBudget = trip.totalBudget?.toBigDecimal()?.toPlainString() ?: "",
                            dailyBudget = trip.dailyBudget?.toBigDecimal()?.toPlainString() ?: ""
                        )
                    )
                }
            }
        }
    }

    fun onTripNameChanged(newName: String) {
        _uiState.update { it.copy(tripUiState = it.tripUiState.copy(tripName = newName)) }
    }

    fun onImageUriChanged(newImageUri: String?) {
        _uiState.update { it.copy(tripUiState = it.tripUiState.copy(imageUri = newImageUri)) }
    }

    fun onImageOffsetChanged(x: Float, y: Float) {
        _uiState.update {
            it.copy(
                tripUiState = it.tripUiState.copy(
                    imageOffsetX = x,
                    imageOffsetY = y
                )
            )
        }
    }

    fun onImageScaleChanged(scale: Float) {
        _uiState.update {
            it.copy(
                tripUiState = it.tripUiState.copy(imageScale = scale)
            )
        }
    }

    fun onDatesChanged(startDate: Long?, endDate: Long?) {
        _uiState.update { currentState ->
            var updatedAddTripState = currentState.tripUiState.copy(
                startDate = startDate,
                endDate = endDate
            )

            // After updating dates, try to recalculate budget
            if (startDate != null && endDate != null) {
                val start = Instant.ofEpochMilli(startDate).atZone(ZoneId.of("UTC")).toLocalDate()
                val end = Instant.ofEpochMilli(endDate).atZone(ZoneId.of("UTC")).toLocalDate()
                val days = ChronoUnit.DAYS.between(start, end) + 1

                if (days > 0) {
                    if (updatedAddTripState.totalBudget.isNotEmpty()) {
                        // Recalculate daily budget from total budget
                        updatedAddTripState = updatedAddTripState.copy(
                            dailyBudget = String.format("%.2f", updatedAddTripState.totalBudget.toDouble() / days)
                        )
                    } else if (updatedAddTripState.dailyBudget.isNotEmpty()) {
                        // Recalculate total budget from daily budget
                        updatedAddTripState = updatedAddTripState.copy(
                            totalBudget = String.format("%.2f", updatedAddTripState.dailyBudget.toDouble() * days)
                        )
                    }
                }
            }
            currentState.copy(tripUiState = updatedAddTripState)
        }
    }

    @Deprecated("Use onCurrencySelected or onCustomCurrencyChanged instead")
    fun onCurrencyChanged(newCurrency: String) {
        _uiState.update { it.copy(tripUiState = it.tripUiState.copy(currency = newCurrency)) }
    }

    fun onCurrencySelected(newCurrency: String) {
        _uiState.update {
            it.copy(
                tripUiState = it.tripUiState.copy(
                    currency = newCurrency,
                    isCurrencyCustom = false
                )
            )
        }
    }

    fun onCustomCurrencyChanged(newCurrency: String) {
        _uiState.update {
            it.copy(
                tripUiState = it.tripUiState.copy(
                    currency = newCurrency,
                    isCurrencyCustom = true
                )
            )
        }
    }

    fun onTotalBudgetChanged(newBudget: String) {
        val addTripUiState = _uiState.value.tripUiState

        val dailyBudget = if (newBudget.isNotEmpty() && addTripUiState.startDate != null && addTripUiState.endDate != null) {
            val start = Instant.ofEpochMilli(addTripUiState.startDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val end = Instant.ofEpochMilli(addTripUiState.endDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val days = ChronoUnit.DAYS.between(start, end) + 1
            if (days > 0) String.format("%.2f", newBudget.toDouble() / days) else ""
        } else {
            addTripUiState.dailyBudget // Keep existing daily budget if no dates
        }

        _uiState.update {
            it.copy(
                tripUiState = it.tripUiState.copy(
                    totalBudget = newBudget,
                    dailyBudget = dailyBudget
                )
            )
        }
    }

    fun onDailyBudgetChanged(newBudget: String) {
        val addTripUiState = _uiState.value.tripUiState

        val totalBudget = if (newBudget.isNotEmpty() && addTripUiState.startDate != null && addTripUiState.endDate != null) {
            val start = Instant.ofEpochMilli(addTripUiState.startDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val end = Instant.ofEpochMilli(addTripUiState.endDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val days = ChronoUnit.DAYS.between(start, end) + 1
            if (days > 0) String.format("%.2f", newBudget.toDouble() * days) else ""
        } else {
            addTripUiState.totalBudget // Keep existing total budget if no dates
        }

        _uiState.update {
            it.copy(
                tripUiState = it.tripUiState.copy(
                    totalBudget = totalBudget,
                    dailyBudget = newBudget
                )
            )
        }
    }

    fun resetAddTripState() {
        _uiState.update { it.copy(tripUiState = TripUiState()) }
    }

    fun addTrip() {
        viewModelScope.launch {
            val addTripState = _uiState.value.tripUiState
            val newTrip = Trip(
                name = addTripState.tripName,
                currency = addTripState.currency,
                isCurrencyCustom = addTripState.isCurrencyCustom,
                imageUri = addTripState.imageUri,
                imageOffsetX = addTripState.imageOffsetX,
                imageOffsetY = addTripState.imageOffsetY,
                imageScale = addTripState.imageScale,
                startDate = addTripState.startDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                endDate = addTripState.endDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                totalBudget = addTripState.totalBudget.toDoubleOrNull(),
                dailyBudget = addTripState.dailyBudget.toDoubleOrNull()
            )
            tripsRepository.addTrip(newTrip)
            resetAddTripState()
        }
    }

    fun updateTrip() {
        viewModelScope.launch {
            val addTripState = _uiState.value.tripUiState
            val updatedTrip = _uiState.value.selectedTrip!!.copy(
                name = addTripState.tripName,
                currency = addTripState.currency,
                isCurrencyCustom = addTripState.isCurrencyCustom,
                imageUri = addTripState.imageUri,
                imageOffsetX = addTripState.imageOffsetX,
                imageOffsetY = addTripState.imageOffsetY,
                imageScale = addTripState.imageScale,
                startDate = addTripState.startDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                endDate = addTripState.endDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                totalBudget = addTripState.totalBudget.toDoubleOrNull(),
                dailyBudget = addTripState.dailyBudget.toDoubleOrNull()
            )
            tripsRepository.updateTrip(updatedTrip)
            resetAddTripState()
        }
    }

    fun unselectTrip() {
        _uiState.update { it.copy(selectedTrip = null) }
    }

    fun selectTrip(trip: Trip) {
        _uiState.update { it.copy(selectedTrip = trip) }
    }

    fun enterSelectionMode(tripId: String) {
        _uiState.update {
            it.copy(selectionMode = true, selectedTrips = it.selectedTrips + tripId)
        }
    }

    fun exitSelectionMode() {
        _uiState.update { it.copy(selectionMode = false, selectedTrips = emptySet()) }
    }

    fun toggleTripSelection(tripId: String) {
        _uiState.update { uiState ->
            val selectedTrips = uiState.selectedTrips
            val newSelectedTrips = if (selectedTrips.contains(tripId)) {
                selectedTrips - tripId
            } else {
                selectedTrips + tripId
            }
            uiState.copy(selectedTrips = newSelectedTrips)
        }
    }

    fun selectAllTrips() {
        _uiState.update { uiState ->
            val allTripIds = uiState.trips.map { it.id }.toSet()
            uiState.copy(selectedTrips = allTripIds)
        }
    }

    fun clearSelectedTrips() {
        _uiState.update { it.copy(selectedTrips = emptySet()) }
    }

    fun deleteSelectedTrips() {
        viewModelScope.launch {
            tripsRepository.deleteTrips(_uiState.value.selectedTrips)
            exitSelectionMode()
        }
    }
}
