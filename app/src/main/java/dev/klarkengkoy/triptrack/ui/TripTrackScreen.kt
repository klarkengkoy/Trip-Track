package dev.klarkengkoy.triptrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.klarkengkoy.triptrack.LoginNavigation
import dev.klarkengkoy.triptrack.MainNavigation
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.ui.components.BottomNavItem
import dev.klarkengkoy.triptrack.ui.components.BottomNavigationBar
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTrackScreen(
    loginViewModel: LoginViewModel,
    snackbarHostState: SnackbarHostState
) {
    val isSignedIn by loginViewModel.isUserSignedIn.collectAsState()

    if (isSignedIn) {
        TripTrackTheme {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            val bottomNavItems = listOf(
                BottomNavItem("trips", R.drawable.travel_luggage_and_bags_24px, "Trips"),
                BottomNavItem("dashboard", R.drawable.dashboard_24px, "Dashboard"),
                BottomNavItem("media", R.drawable.photo_album_24px, "Media"),
                BottomNavItem("maps", R.drawable.map_24px, "Maps"),
                BottomNavItem("settings", R.drawable.settings_24px, "Settings")
            )

            val shouldShowBottomNav = bottomNavItems.any { it.route == navBackStackEntry?.destination?.route }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    AnimatedVisibility(
                        visible = shouldShowBottomNav,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        BottomNavigationBar(
                            navController = navController,
                            items = bottomNavItems,
                        )
                    }
                }
            ) { innerPadding ->
                MainNavigation(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    innerPadding = innerPadding
                )
            }
        }
    } else {
        LoginNavigation(viewModel = loginViewModel)
    }
}
