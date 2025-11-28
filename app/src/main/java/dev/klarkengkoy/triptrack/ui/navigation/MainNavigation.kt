package dev.klarkengkoy.triptrack.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.ui.TopAppBarState
import dev.klarkengkoy.triptrack.ui.dashboard.DashboardScreen
import dev.klarkengkoy.triptrack.ui.maps.MapsScreen
import dev.klarkengkoy.triptrack.ui.media.MediaScreen
import dev.klarkengkoy.triptrack.ui.settings.SettingsScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import dev.klarkengkoy.triptrack.ui.trips.transaction.CategoryScreen
import dev.klarkengkoy.triptrack.ui.trips.transaction.TransactionScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.CurrencyListScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripBudgetScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripCurrencyScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripDatesScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripNameScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripPhotoScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripSummaryScreen

@Composable
fun MainNavigation(
    navigationState: NavigationState,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    setTopAppBar: (TopAppBarState) -> Unit
) {
    val backStack = navigationState.backStacks[navigationState.topLevelRoute]
        ?: remember { mutableStateListOf() }

    val entryProvider = entryProvider {
        // --- Main Tabs ---
        entry<Trips> { key ->
            TripsScreen(
                contentPadding = contentPadding,
                setTopAppBar = setTopAppBar,
                onAddTrip = { navigator.navigate(AddTripName) },
                onAddTransaction = { tripId -> navigator.navigate(AddTransactionCategory(tripId)) },
                onEditTrip = { tripId -> navigator.navigate(EditTripName(tripId)) },
                onEditTransaction = { transactionId -> navigator.navigate(EditTransactionDetails(transactionId)) }
            )
        }
        entry<Dashboard> { DashboardScreen() }
        entry<Media> { MediaScreen() }
        entry<Maps> { MapsScreen() }
        entry<Settings> { SettingsScreen(setTopAppBar = setTopAppBar) }

        // --- Add Trip Wizard ---
        entry<AddTripName> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripNameScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(AddTripCurrency) },
                viewModel = viewModel
            )
        }
        entry<AddTripCurrency> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripCurrencyScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(AddTripDates) },
                onCurrencyClick = { navigator.navigate(CurrencyList) },
                viewModel = viewModel
            )
        }
        entry<AddTripDates> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripDatesScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(AddTripBudget) },
                viewModel = viewModel
            )
        }
        entry<AddTripBudget> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripBudgetScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(AddTripPhoto) },
                viewModel = viewModel
            )
        }
        entry<AddTripPhoto> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripPhotoScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(AddTripSummary) },
                viewModel = viewModel
            )
        }
        entry<AddTripSummary> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripSummaryScreen(
                contentPadding = contentPadding,
                onSaveTrip = {
                    viewModel.addTrip()
                    navigator.backToRoot()
                },
                onDiscard = {
                    viewModel.resetAddTripState()
                    navigator.backToRoot()
                },
                viewModel = viewModel,
                saveButtonText = "Save Trip"
            )
        }

        // --- Edit Trip Wizard ---
        entry<EditTripName> { key ->
            val viewModel: TripsViewModel = hiltViewModel()
            val tripId = key.tripId

            LaunchedEffect(tripId) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
                if (tripId.isNotEmpty()) {
                    viewModel.populateTripDetails(tripId)
                }
            }
            TripNameScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(EditTripCurrency) },
                viewModel = viewModel
            )
        }
        entry<EditTripCurrency> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripCurrencyScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(EditTripDates) },
                onCurrencyClick = { navigator.navigate(CurrencyList) },
                viewModel = viewModel
            )
        }
        entry<EditTripDates> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripDatesScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(EditTripBudget) },
                viewModel = viewModel
            )
        }
        entry<EditTripBudget> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripBudgetScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(EditTripPhoto) },
                viewModel = viewModel
            )
        }
        entry<EditTripPhoto> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripPhotoScreen(
                contentPadding = contentPadding,
                onNavigateNext = { navigator.navigate(EditTripSummary) },
                viewModel = viewModel
            )
        }
        entry<EditTripSummary> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TripSummaryScreen(
                contentPadding = contentPadding,
                onSaveTrip = {
                    viewModel.updateTrip()
                    navigator.backToRoot()
                },
                onDiscard = {
                    viewModel.resetAddTripState()
                    navigator.backToRoot()
                },
                viewModel = viewModel,
                saveButtonText = "Update Trip"
            )
        }

        // --- Shared ---
        entry<CurrencyList> {
            val viewModel: TripsViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Select Currency") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            CurrencyListScreen(
                onCurrencySelected = {
                    viewModel.onCurrencySelected(it)
                    navigator.goBack()
                }
            )
        }

        // --- Transactions ---
        entry<AddTransactionCategory> { key ->
            // This entry point carries the tripId context
            val tripId = key.tripId
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Select a Category") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            CategoryScreen(
                contentPadding = contentPadding,
                onCategorySelected = { category ->
                navigator.navigate(AddTransactionDetails(tripId, category))
            })
        }
        entry<AddTransactionDetails> { key ->
            // key contains tripId and category
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Transaction") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TransactionScreen(
                tripId = key.tripId,
                category = key.category,
                contentPadding = contentPadding,
                onSave = {
                    navigator.goBack() // Pop details
                    navigator.goBack() // Pop category selection
                },
                onCategoryClick = {
                    navigator.goBack() // Go back to category selection
                }
            )
        }
        entry<EditTransactionDetails> { key ->
            val transactionId = key.transactionId
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Transaction") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            TransactionScreen(
                transactionId = transactionId,
                contentPadding = contentPadding,
                onSave = { navigator.goBack() },
                onCategoryClick = {
                    navigator.navigate(EditTransactionCategory(transactionId))
                }
            )
        }
        entry<EditTransactionCategory> { key ->
            // val transactionId = key.transactionId
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Select a Category") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.goBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_navigate_up))
                        }
                    }
                ))
            }
            CategoryScreen(
                contentPadding = contentPadding,
                onCategorySelected = { category ->
                // Nav 2 approach was passing back via SavedStateHandle.
                // In Nav 3, we might need a result mechanism or shared state.
                // For now, mimic "pop" behavior.
                // Ideally, we update ViewModel state here directly since we have transactionId?
                // Or standard pop.
                navigator.goBack()
            })
        }
    }

    Surface(modifier = modifier.background(Color.Red)) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider,
            modifier = Modifier.fillMaxSize()
        )
    }
}
