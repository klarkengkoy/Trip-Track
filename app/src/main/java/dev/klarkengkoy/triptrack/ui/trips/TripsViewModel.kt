package dev.klarkengkoy.triptrack.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class TripsUiState(
    val selectedTrip: Trip? = null,
    val trips: List<Trip> = emptyList(),
    val transactionsByTrip: Map<String, List<Transaction>> = emptyMap(),
    val addTripUiState: AddTripUiState = AddTripUiState()
) {
    val selectedTripTransactions: List<Transaction>
        get() = selectedTrip?.let { transactionsByTrip[it.id] }.orEmpty()
}

data class AddTripUiState(
    val tripName: String = "",
    val imageUri: String? = null,
    val imageOffsetX: Float = 0f,
    val imageOffsetY: Float = 0f,
    val imageScale: Float = 1f,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val currency: String = "",
    val isCurrencyCustom: Boolean = false,
    val totalBudget: Double? = null,
    val dailyBudget: Double? = null
)

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val tripsRepository: TripsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        tripsRepository.getTrips()
            .onEach { trips ->
                _uiState.update { it.copy(trips = trips) }
            }
            .launchIn(viewModelScope)
    }

    fun onTripNameChanged(newName: String) {
        _uiState.update { it.copy(addTripUiState = it.addTripUiState.copy(tripName = newName)) }
    }

    fun onImageUriChanged(newImageUri: String?) {
        _uiState.update { it.copy(addTripUiState = it.addTripUiState.copy(imageUri = newImageUri)) }
    }

    fun onImageOffsetChanged(x: Float, y: Float) {
        _uiState.update {
            it.copy(
                addTripUiState = it.addTripUiState.copy(
                    imageOffsetX = x,
                    imageOffsetY = y
                )
            )
        }
    }

    fun onImageScaleChanged(scale: Float) {
        _uiState.update {
            it.copy(
                addTripUiState = it.addTripUiState.copy(imageScale = scale)
            )
        }
    }

    fun onDatesChanged(startDate: Long?, endDate: Long?) {
        _uiState.update {
            it.copy(
                addTripUiState = it.addTripUiState.copy(
                    startDate = startDate,
                    endDate = endDate
                )
            )
        }
    }

    @Deprecated("Use onCurrencySelected or onCustomCurrencyChanged instead")
    fun onCurrencyChanged(newCurrency: String) {
        _uiState.update { it.copy(addTripUiState = it.addTripUiState.copy(currency = newCurrency)) }
    }

    fun onCurrencySelected(newCurrency: String) {
        _uiState.update {
            it.copy(
                addTripUiState = it.addTripUiState.copy(
                    currency = newCurrency,
                    isCurrencyCustom = false
                )
            )
        }
    }

    fun onCustomCurrencyChanged(newCurrency: String) {
        _uiState.update {
            it.copy(
                addTripUiState = it.addTripUiState.copy(
                    currency = newCurrency,
                    isCurrencyCustom = true
                )
            )
        }
    }

    fun onTotalBudgetChanged(newBudget: String) {
        val totalBudget = newBudget.toDoubleOrNull()
        val addTripUiState = _uiState.value.addTripUiState

        val dailyBudget = if (totalBudget != null && addTripUiState.startDate != null && addTripUiState.endDate != null) {
            val start = Instant.ofEpochMilli(addTripUiState.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val end = Instant.ofEpochMilli(addTripUiState.endDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val days = ChronoUnit.DAYS.between(start, end) + 1
            if (days > 0) totalBudget / days else null
        } else {
            addTripUiState.dailyBudget // Keep existing daily budget if no dates
        }

        _uiState.update {
            it.copy(
                addTripUiState = it.addTripUiState.copy(
                    totalBudget = totalBudget,
                    dailyBudget = dailyBudget
                )
            )
        }
    }

    fun onDailyBudgetChanged(newBudget: String) {
        val dailyBudget = newBudget.toDoubleOrNull()
        val addTripUiState = _uiState.value.addTripUiState

        val totalBudget = if (dailyBudget != null && addTripUiState.startDate != null && addTripUiState.endDate != null) {
            val start = Instant.ofEpochMilli(addTripUiState.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val end = Instant.ofEpochMilli(addTripUiState.endDate).atZone(ZoneId.systemDefault()).toLocalDate()
            val days = ChronoUnit.DAYS.between(start, end) + 1
            if (days > 0) dailyBudget * days else null
        } else {
            addTripUiState.totalBudget // Keep existing total budget if no dates
        }

        _uiState.update {
            it.copy(
                addTripUiState = it.addTripUiState.copy(
                    totalBudget = totalBudget,
                    dailyBudget = dailyBudget
                )
            )
        }
    }


    fun addTrip() {
        viewModelScope.launch {
            val newTrip = Trip(
                name = _uiState.value.addTripUiState.tripName,
                currency = _uiState.value.addTripUiState.currency,
                imageUri = _uiState.value.addTripUiState.imageUri,
                imageOffsetX = _uiState.value.addTripUiState.imageOffsetX,
                imageOffsetY = _uiState.value.addTripUiState.imageOffsetY,
                imageScale = _uiState.value.addTripUiState.imageScale,
                startDate = _uiState.value.addTripUiState.startDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() },
                endDate = _uiState.value.addTripUiState.endDate?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() },
                totalBudget = _uiState.value.addTripUiState.totalBudget,
                dailyBudget = _uiState.value.addTripUiState.dailyBudget
            )
            tripsRepository.addTrip(newTrip)
            _uiState.update { it.copy(addTripUiState = AddTripUiState()) } // Reset the form
        }
    }

    fun unselectTrip() {
        _uiState.update { it.copy(selectedTrip = null) }
    }

    fun selectTrip(trip: Trip) {
        _uiState.update { it.copy(selectedTrip = trip) }
    }
}