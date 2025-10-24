package dev.klarkengkoy.triptrack

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.klarkengkoy.triptrack.ui.dashboard.DashboardScreen
import dev.klarkengkoy.triptrack.ui.media.MediaScreen
import dev.klarkengkoy.triptrack.ui.maps.MapsScreen
import dev.klarkengkoy.triptrack.ui.settings.SettingsScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsScreen

@Composable
fun MainNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues
) {
    NavHost(navController, startDestination = "trips", modifier = modifier) {
        composable("trips") {
            TripsScreen(modifier = Modifier.padding(innerPadding))
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
    }
}
