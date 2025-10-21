package dev.klarkengkoy.triptrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.klarkengkoy.triptrack.ui.dashboard.DashboardScreen
import dev.klarkengkoy.triptrack.ui.home.HomeScreen
import dev.klarkengkoy.triptrack.ui.notifications.NotificationsScreen
import dev.klarkengkoy.triptrack.ui.settings.SettingsScreen

@Composable
fun MobileNavigation(navController: NavHostController, onToggleTheme: () -> Unit, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier = modifier) {
        composable("home") {
            HomeScreen(onToggleTheme = onToggleTheme)
        }
        composable("dashboard") {
            DashboardScreen()
        }
        composable("notifications") {
            NotificationsScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}
