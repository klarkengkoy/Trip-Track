package dev.klarkengkoy.triptrack.ui.trips

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

data class TripsUiState(
    // Flag to indicate if the initial data has loaded, to prevent UI flicker
    val isDataLoaded: Boolean = false,
    val selectedTrip: Trip? = null,
    val trips: List<Trip> = emptyList(),
    val selectedTripTransactions: List<Transaction> = emptyList(), // Directly exposed
    val tripUiState: TripUiState = TripUiState(),
    val selectionMode: Boolean = false,
    val selectedTrips: Set<String> = emptySet()
)

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

    // Private mutable states for UI-driven actions
    private val _tripUiState = MutableStateFlow(TripUiState())
    private val _selectionMode = MutableStateFlow(false)
    private val _selectedTrips = MutableStateFlow<Set<String>>(emptySet())
    private val _manualSelectedTripId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _transactionsFlow = combine(
        tripsRepository.getActiveTrip(),
        _manualSelectedTripId
    ) { activeTrip, manualSelectedId ->
        manualSelectedId ?: activeTrip?.id
    }.flatMapLatest { tripId ->
        if (tripId != null) {
            tripsRepository.getTransactions(tripId)
        } else {
            flowOf(emptyList())
        }
    }

    // Helper data classes for combine to avoid tuple limitation
    private data class TripsData(
        val trips: List<Trip>,
        val activeTrip: Trip?,
        val manualSelectedId: String?,
        val transactions: List<Transaction>
    )

    private data class TripsUiInput(
        val tripUiState: TripUiState,
        val selectionMode: Boolean,
        val selectedTrips: Set<String>
    )

    // The main public UI state, derived reactively from multiple flows
    val uiState: StateFlow<TripsUiState> = combine(
        tripsRepository.getTrips(),
        tripsRepository.getActiveTrip(),
        _manualSelectedTripId,
        _transactionsFlow
    ) { trips, activeTrip, manualSelectedId, transactions ->
        TripsData(trips, activeTrip, manualSelectedId, transactions)
    }.combine(
        combine(
            _tripUiState,
            _selectionMode,
            _selectedTrips
        ) { tripUiState, selectionMode, selectedTrips ->
            TripsUiInput(tripUiState, selectionMode, selectedTrips)
        }
    ) { data, ui ->
        val finalSelectedTrip = if (data.manualSelectedId != null) {
            data.trips.find { it.id == data.manualSelectedId }
        } else {
            data.activeTrip
        }

        TripsUiState(
            isDataLoaded = true, // We only emit once all streams are ready
            selectedTrip = finalSelectedTrip,
            trips = data.trips,
            selectedTripTransactions = data.transactions,
            tripUiState = ui.tripUiState,
            selectionMode = ui.selectionMode,
            selectedTrips = ui.selectedTrips
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TripsUiState() // Initial state shows loading/empty
    )

    init {
        // Handle incoming navigation arguments
        val navTripId = savedStateHandle.get<String>("tripId")
        if (navTripId != null) {
            _manualSelectedTripId.value = navTripId
        }
    }

    fun onTripClicked(tripId: String) {
        viewModelScope.launch {
            // Set the trip as active in our DataStore
            tripsRepository.setActiveTrip(tripId, true)
            // NOTE: We do NOT clear the manual override here anymore.
            // Clearing it causes a race condition where 'manualSelectedId' becomes null
            // BEFORE 'activeTrip' from DataStore updates, causing a momentary null selectedTrip.
        }
    }

    fun onTripDeactivated() {
        viewModelScope.launch {
            val tripToDeactivate = uiState.value.selectedTrip
            if (tripToDeactivate != null) {
                tripsRepository.setActiveTrip(tripToDeactivate.id, false)
            }
            // Also clear manual selection when deactivating
            _manualSelectedTripId.value = null
        }
    }

    fun unselectTrip() {
        // This is called on "Back" press from Trip Details.
        // It should clear the DataStore active trip.
        onTripDeactivated()
    }
    
    fun selectTrip(trip: Trip) {
        // This is a more explicit selection, let's treat it as a manual override for the session.
        _manualSelectedTripId.value = trip.id
    }

    fun populateTripDetails(tripId: String) {
        viewModelScope.launch {
            val trip = tripsRepository.getTrip(tripId)
            if (trip != null) {
                // When editing a trip, we manually select it and populate the form state
                _manualSelectedTripId.value = tripId
                _tripUiState.update {
                    it.copy(
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
                }
            }
        }
    }

    fun onTripNameChanged(newName: String) {
        _tripUiState.update { it.copy(tripName = newName) }
    }

    fun onImageUriChanged(newImageUri: String?) {
        _tripUiState.update { it.copy(imageUri = newImageUri) }
    }

    fun onImageOffsetChanged(x: Float, y: Float) {
        _tripUiState.update { it.copy(imageOffsetX = x, imageOffsetY = y) }
    }

    fun onImageScaleChanged(scale: Float) {
        _tripUiState.update { it.copy(imageScale = scale) }
    }

    fun onDatesChanged(startDate: Long?, endDate: Long?) {
        _tripUiState.update { currentState ->
            var updatedState = currentState.copy(startDate = startDate, endDate = endDate)
            if (startDate != null && endDate != null) {
                val start = Instant.ofEpochMilli(startDate).atZone(ZoneId.of("UTC")).toLocalDate()
                val end = Instant.ofEpochMilli(endDate).atZone(ZoneId.of("UTC")).toLocalDate()
                val days = ChronoUnit.DAYS.between(start, end) + 1
                if (days > 0) {
                    if (updatedState.totalBudget.isNotEmpty()) {
                        updatedState = updatedState.copy(
                            dailyBudget = String.format(Locale.US, "%.2f", updatedState.totalBudget.toDouble() / days)
                        )
                    } else if (updatedState.dailyBudget.isNotEmpty()) {
                        updatedState = updatedState.copy(
                            totalBudget = String.format(Locale.US, "%.2f", updatedState.dailyBudget.toDouble() * days)
                        )
                    }
                }
            }
            updatedState
        }
    }

    fun onCurrencySelected(newCurrency: String) {
        _tripUiState.update { it.copy(currency = newCurrency, isCurrencyCustom = false) }
    }

    fun onCustomCurrencyChanged(newCurrency: String) {
        _tripUiState.update { it.copy(currency = newCurrency, isCurrencyCustom = true) }
    }

    fun onTotalBudgetChanged(newBudget: String) {
        val currentState = _tripUiState.value
        val dailyBudget = if (newBudget.isNotEmpty() && currentState.startDate != null && currentState.endDate != null) {
            val start = Instant.ofEpochMilli(currentState.startDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val end = Instant.ofEpochMilli(currentState.endDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val days = ChronoUnit.DAYS.between(start, end) + 1
            if (days > 0) String.format(Locale.US, "%.2f", newBudget.toDouble() / days) else ""
        } else {
            currentState.dailyBudget
        }
        _tripUiState.update { it.copy(totalBudget = newBudget, dailyBudget = dailyBudget) }
    }

    fun onDailyBudgetChanged(newBudget: String) {
        val currentState = _tripUiState.value
        val totalBudget = if (newBudget.isNotEmpty() && currentState.startDate != null && currentState.endDate != null) {
            val start = Instant.ofEpochMilli(currentState.startDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val end = Instant.ofEpochMilli(currentState.endDate).atZone(ZoneId.of("UTC")).toLocalDate()
            val days = ChronoUnit.DAYS.between(start, end) + 1
            if (days > 0) String.format(Locale.US, "%.2f", newBudget.toDouble() * days) else ""
        } else {
            currentState.totalBudget
        }
        _tripUiState.update { it.copy(totalBudget = totalBudget, dailyBudget = newBudget) }
    }

    fun resetAddTripState() {
        _tripUiState.value = TripUiState()
    }

    fun addTrip() {
        viewModelScope.launch {
            val state = _tripUiState.value
            val newTrip = Trip(
                name = state.tripName,
                currency = state.currency,
                isCurrencyCustom = state.isCurrencyCustom,
                imageUri = state.imageUri,
                imageOffsetX = state.imageOffsetX,
                imageOffsetY = state.imageOffsetY,
                imageScale = state.imageScale,
                startDate = state.startDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                endDate = state.endDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                totalBudget = state.totalBudget.toDoubleOrNull(),
                dailyBudget = state.dailyBudget.toDoubleOrNull()
            )
            tripsRepository.addTrip(newTrip)
            resetAddTripState()
        }
    }

    fun updateTrip() {
        viewModelScope.launch {
            val state = _tripUiState.value
            val currentSelected = uiState.value.selectedTrip ?: return@launch
            val updatedTrip = currentSelected.copy(
                name = state.tripName,
                currency = state.currency,
                isCurrencyCustom = state.isCurrencyCustom,
                imageUri = state.imageUri,
                imageOffsetX = state.imageOffsetX,
                imageOffsetY = state.imageOffsetY,
                imageScale = state.imageScale,
                startDate = state.startDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                endDate = state.endDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate() },
                totalBudget = state.totalBudget.toDoubleOrNull(),
                dailyBudget = state.dailyBudget.toDoubleOrNull()
            )
            tripsRepository.updateTrip(updatedTrip)
            resetAddTripState()
        }
    }

    fun enterSelectionMode(tripId: String) {
        _selectionMode.value = true
        _selectedTrips.update { it + tripId }
    }

    fun exitSelectionMode() {
        _selectionMode.value = false
        _selectedTrips.value = emptySet()
    }

    fun toggleTripSelection(tripId: String) {
        _selectedTrips.update {
            if (it.contains(tripId)) it - tripId else it + tripId
        }
    }

    fun selectAllTrips() {
        _selectedTrips.value = uiState.value.trips.map { it.id }.toSet()
    }

    fun clearSelectedTrips() {
        _selectedTrips.value = emptySet()
    }

    fun deleteSelectedTrips() {
        viewModelScope.launch {
            tripsRepository.deleteTrips(_selectedTrips.value)
            exitSelectionMode()
        }
    }
}
