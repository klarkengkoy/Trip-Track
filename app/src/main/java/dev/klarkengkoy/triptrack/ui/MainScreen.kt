package dev.klarkengkoy.triptrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.ui.components.BottomNavItem
import dev.klarkengkoy.triptrack.ui.components.BottomNavigationBar
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.navigation.LoginNavigation
import dev.klarkengkoy.triptrack.ui.navigation.MainNavigation
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

/**
 * The main entry point for the TripTrack UI.
 * This composable observes the user's sign-in state and displays either the
 * main app content (with bottom navigation) or the login flow.
 */
@Composable
fun TripTrackScreen(
    loginViewModel: LoginViewModel,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val isSignedIn by loginViewModel.isUserSignedIn.collectAsStateWithLifecycle()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val activeTripUiState by mainViewModel.activeTripUiState.collectAsStateWithLifecycle()

    if (isSignedIn) {
        MainScreen(
            snackbarHostState = snackbarHostState,
            navController = navController,
            mainUiState = mainUiState,
            activeTripUiState = activeTripUiState,
            setTopAppBar = { mainViewModel.setTopAppBarState(it.title, it.navigationIcon, it.actions, it.isCenterAligned) }
        )
    } else {
        LoginNavigation(viewModel = loginViewModel)
    }
}

/**
 * The main UI scaffold, including the bottom navigation bar and the NavHost
 * for all screens accessible after login.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
    mainUiState: MainUiState,
    activeTripUiState: ActiveTripUiState,
    setTopAppBar: (TopAppBarState) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val bottomNavItems = listOf(
        BottomNavItem("trips", R.drawable.travel_luggage_and_bags_24px, "Trips"),
        BottomNavItem("dashboard", R.drawable.dashboard_24px, "Dashboard", isEnabled = activeTripUiState.hasActiveTrip),
        BottomNavItem("media", R.drawable.photo_album_24px, "Media", isEnabled = activeTripUiState.hasActiveTrip),
        BottomNavItem("maps", R.drawable.map_24px, "Maps", isEnabled = activeTripUiState.hasActiveTrip),
        BottomNavItem("settings", R.drawable.settings_24px, "Settings")
    )

    val isPreview = LocalInspectionMode.current
    val shouldShowBottomNav = if (isPreview) {
        true
    } else {
        bottomNavItems.any { navBackStackEntry?.destination?.route?.startsWith(it.route) == true }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = mainUiState.topAppBarState.title,
                navigationIcon = mainUiState.topAppBarState.navigationIcon,
                actions = mainUiState.topAppBarState.actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Use a Box to prevent Scaffold from crashing in previews when the bar is hidden.
            Box {
                AnimatedVisibility(
                    visible = shouldShowBottomNav,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomNavigationBar(
                        navController = navController,
                        items = bottomNavItems
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isPreview) {
            // In preview, show a mock list instead of the complex NavHost
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Main Content Area")
                Text("(This is a preview)")
            }
        } else {
            MainNavigation(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                setTopAppBar = setTopAppBar
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    TripTrackTheme {
        MainScreen(
            snackbarHostState = remember { SnackbarHostState() },
            navController = rememberNavController(),
            mainUiState = MainUiState(),
            activeTripUiState = ActiveTripUiState(),
            setTopAppBar = {}
        )
    }
}
