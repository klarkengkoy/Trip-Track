package dev.klarkengkoy.triptrack.ui.trips

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import dev.klarkengkoy.triptrack.model.Trip
import dev.klarkengkoy.triptrack.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TripsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val tripsRepository: TripsRepository = mockk()
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()

    @Test
    fun `uiState loads trips from repository`() = runTest {
        // Given
        val testTrips = listOf(
            Trip(id = "1", name = "Paris 2024"),
            Trip(id = "2", name = "Tokyo 2025")
        )
        coEvery { tripsRepository.getTrips() } returns flowOf(testTrips)
        coEvery { tripsRepository.getActiveTrip() } returns flowOf(null)

        // When
        val viewModel = TripsViewModel(tripsRepository, savedStateHandle)

        // Then
        viewModel.uiState.test {
            // With UnconfinedTestDispatcher and flowOf, the initial state is likely skipped
            // or the flow emits the calculated state immediately as the first item.
            val state = awaitItem()

            // If the first state is the loading state, await the next one
            val loadedState = if (!state.isDataLoaded) awaitItem() else state

            assertEquals(testTrips, loadedState.trips)
            assert(loadedState.isDataLoaded)
        }
    }
}
