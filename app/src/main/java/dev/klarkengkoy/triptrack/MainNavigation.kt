package dev.klarkengkoy.triptrack

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.klarkengkoy.triptrack.ui.dashboard.DashboardScreen
import dev.klarkengkoy.triptrack.ui.maps.MapsScreen
import dev.klarkengkoy.triptrack.ui.media.MediaScreen
import dev.klarkengkoy.triptrack.ui.settings.SettingsScreen
import dev.klarkengkoy.triptrack.ui.trips.AddTransactionScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import dev.klarkengkoy.triptrack.ui.trips.addtrip.AddTripBudgetScreen
import dev.klarkengkoy.triptrack.ui.trips.addtrip.AddTripCurrencyScreen
import dev.klarkengkoy.triptrack.ui.trips.addtrip.AddTripDatesScreen
import dev.klarkengkoy.triptrack.ui.trips.addtrip.AddTripNameScreen
import dev.klarkengkoy.triptrack.ui.trips.addtrip.AddTripPhotoScreen
import dev.klarkengkoy.triptrack.ui.trips.addtrip.AddTripSummaryScreen
import dev.klarkengkoy.triptrack.ui.trips.addtrip.CurrencyListScreen

const val ADD_TRIP_ROUTE = "addTrip"

@Composable
fun MainNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    NavHost(navController, startDestination = "trips", modifier = modifier) {
        composable("trips") {
            TripsScreen(
                modifier = Modifier.padding(innerPadding),
                onAddTrip = { navController.navigate(ADD_TRIP_ROUTE) },
                onAddTransaction = { tripId -> navController.navigate("addTransaction/$tripId") }
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
            SettingsScreen()
        }
        navigation(startDestination = "addTripName", route = ADD_TRIP_ROUTE) {
            composable("addTripName") {
                val backStackEntry = remember(it) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                AddTripNameScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateNext = { navController.navigate("addTripCurrency") },
                    viewModel = viewModel
                )
            }
            composable("addTripCurrency") {
                val backStackEntry = remember(it) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                AddTripCurrencyScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateNext = { navController.navigate("addTripDates") },
                    onCurrencyClick = { navController.navigate("currencyList") },
                    viewModel = viewModel
                )
            }
            composable("addTripDates") {
                val backStackEntry = remember(it) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                AddTripDatesScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateNext = { navController.navigate("addTripBudget") },
                    viewModel = viewModel
                )
            }
            composable("addTripBudget") {
                val backStackEntry = remember(it) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                AddTripBudgetScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateNext = { navController.navigate("addTripPhoto") },
                    viewModel = viewModel
                )
            }
            composable("addTripPhoto") {
                val backStackEntry = remember(it) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                AddTripPhotoScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateNext = { navController.navigate("addTripSummary") },
                    viewModel = viewModel
                )
            }
            composable("addTripSummary") {
                val backStackEntry = remember(it) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                AddTripSummaryScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onSaveTrip = {
                        viewModel.addTrip()
                        navController.navigate("trips") {
                            popUpTo(ADD_TRIP_ROUTE) { inclusive = true }
                        }
                    },
                    onDiscard = {
                        navController.navigate("trips") {
                            popUpTo(ADD_TRIP_ROUTE) { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }
            composable("currencyList") {
                val backStackEntry = remember(it) { navController.getBackStackEntry(ADD_TRIP_ROUTE) }
                val viewModel: TripsViewModel = hiltViewModel(backStackEntry)
                CurrencyListScreen(
                    onNavigateUp = { navController.navigateUp() },
                    onCurrencySelected = {
                        viewModel.onCurrencyChanged(it)
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
