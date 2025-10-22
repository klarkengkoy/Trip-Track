package dev.klarkengkoy.triptrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.klarkengkoy.triptrack.ui.dashboard.DashboardScreen
import dev.klarkengkoy.triptrack.ui.trips.TripsScreen
import dev.klarkengkoy.triptrack.ui.media.MediaScreen
import dev.klarkengkoy.triptrack.ui.maps.MapsScreen
import dev.klarkengkoy.triptrack.ui.settings.SettingsScreen

@Composable
fun MobileNavigation(navController: NavHostController, onToggleTheme: () -> Unit, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "trips", modifier = modifier) {
        composable("trips") {
            TripsScreen(onToggleTheme = onToggleTheme)
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
