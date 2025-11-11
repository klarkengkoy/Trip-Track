package dev.klarkengkoy.triptrack.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.ui.MainViewModel
import dev.klarkengkoy.triptrack.ui.dashboard.DashboardScreen
import dev.klarkengkoy.triptrack.ui.maps.MapsScreen
import dev.klarkengkoy.triptrack.ui.media.MediaScreen
import dev.klarkengkoy.triptrack.ui.settings.SettingsScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import dev.klarkengkoy.triptrack.ui.trips.transaction.AddTransactionScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.AddTripBudgetScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.AddTripCurrencyScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.AddTripDatesScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.AddTripNameScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.AddTripPhotoScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.AddTripSummaryScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.CurrencyListScreen

const val ADD_TRIP_ROUTE = "addTrip"
const val EDIT_TRIP_ROUTE = "editTrip"

@Composable
fun MainNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    NavHost(navController, startDestination = "trips", modifier = modifier) {
        composable("trips") {
            TripsScreen(
                mainViewModel = mainViewModel,
                onAddTrip = { navController.navigate(ADD_TRIP_ROUTE) },
                onAddTransaction = { tripId -> navController.navigate("addTransaction/$tripId") },
                onEditTrip = { tripId -> navController.navigate("$EDIT_TRIP_ROUTE/$tripId") }
            )
        }
        composable("dashboard") {
            DashboardScreen()
        }
        composable("media") {
            MediaScreen()
        }
        composable("maps") {
            MapsScreen()
        }
        composable("settings") {
            SettingsScreen(mainViewModel = mainViewModel)
        }
        navigation(startDestination = "addTripName", route = ADD_TRIP_ROUTE) {
            composable("addTripName") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Add Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripNameScreen(
                    onNavigateNext = { navController.navigate("addTripCurrency") },
                    viewModel = viewModel
                )
            }
            composable("addTripCurrency") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Add Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripCurrencyScreen(
                    onNavigateNext = { navController.navigate("addTripDates") },
                    onCurrencyClick = { navController.navigate("currencyList") },
                    viewModel = viewModel
                )
            }
            composable("addTripDates") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Add Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripDatesScreen(
                    onNavigateNext = { navController.navigate("addTripBudget") },
                    viewModel = viewModel
                )
            }
            composable("addTripBudget") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Add Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripBudgetScreen(
                    onNavigateNext = { navController.navigate("addTripPhoto") },
                    viewModel = viewModel
                )
            }
            composable("addTripPhoto") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Add Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripPhotoScreen(
                    onNavigateNext = { navController.navigate("addTripSummary") },
                    viewModel = viewModel
                )
            }
            composable("addTripSummary") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Add Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripSummaryScreen(
                    onSaveTrip = {
                        viewModel.addTrip()
                        navController.navigate("trips") {
                            popUpTo(ADD_TRIP_ROUTE) { inclusive = true }
                        }
                    },
                    onDiscard = {
                        viewModel.resetAddTripState()
                        navController.navigate("trips") {
                            popUpTo(ADD_TRIP_ROUTE) { inclusive = true }
                        }
                    },
                    viewModel = viewModel,
                    saveButtonText = "Save Trip"
                )
            }
            composable("currencyList") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Select Currency") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                CurrencyListScreen(
                    onCurrencySelected = {
                        viewModel.onCurrencySelected(it)
                        navController.navigateUp()
                    }
                )
            }
        }
        navigation(startDestination = "addTripName", route = "$EDIT_TRIP_ROUTE/{tripId}") {
            composable("addTripName") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

                LaunchedEffect(tripId) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Edit Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )

                    if (tripId.isNotEmpty()) {
                        viewModel.populateTripDetails(tripId)
                    }
                }

                AddTripNameScreen(
                    onNavigateNext = { navController.navigate("addTripCurrency") },
                    viewModel = viewModel
                )
            }
            composable("addTripCurrency") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Edit Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripCurrencyScreen(
                    onNavigateNext = { navController.navigate("addTripDates") },
                    onCurrencyClick = { navController.navigate("currencyList") },
                    viewModel = viewModel
                )
            }
            composable("addTripDates") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Edit Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripDatesScreen(
                    onNavigateNext = { navController.navigate("addTripBudget") },
                    viewModel = viewModel
                )
            }
            composable("addTripBudget") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Edit Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripBudgetScreen(
                    onNavigateNext = { navController.navigate("addTripPhoto") },
                    viewModel = viewModel
                )
            }
            composable("addTripPhoto") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Edit Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripPhotoScreen(
                    onNavigateNext = { navController.navigate("addTripSummary") },
                    viewModel = viewModel
                )
            }
            composable("addTripSummary") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Edit Trip") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                AddTripSummaryScreen(
                    onSaveTrip = {
                        viewModel.updateTrip()
                        navController.navigate("trips") {
                            popUpTo("$EDIT_TRIP_ROUTE/{tripId}") { inclusive = true }
                        }
                    },
                    onDiscard = {
                        viewModel.resetAddTripState()
                        navController.navigate("trips") {
                            popUpTo("$EDIT_TRIP_ROUTE/{tripId}") { inclusive = true }
                        }
                    },
                    viewModel = viewModel,
                    saveButtonText = "Update Trip"
                )
            }
            composable("currencyList") { entry ->
                val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

                LaunchedEffect(Unit) {
                    mainViewModel.setTopAppBarState(
                        title = { Text("Select Currency") },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_navigate_up)
                                )
                            }
                        },
                        actions = {}
                    )
                }

                CurrencyListScreen(
                    onCurrencySelected = {
                        viewModel.onCurrencySelected(it)
                        navController.navigateUp()
                    }
                )
            }
        }
        composable("addTransaction/{tripId}") {
            AddTransactionScreen()
        }
    }
}
