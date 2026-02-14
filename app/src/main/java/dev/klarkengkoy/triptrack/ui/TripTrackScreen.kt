package dev.klarkengkoy.triptrack.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.ui.components.BottomNavItem
import dev.klarkengkoy.triptrack.ui.components.BottomNavigationBar
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.navigation.Dashboard
import dev.klarkengkoy.triptrack.ui.navigation.LoginNavigation
import dev.klarkengkoy.triptrack.ui.navigation.MainNavigation
import dev.klarkengkoy.triptrack.ui.navigation.Maps
import dev.klarkengkoy.triptrack.ui.navigation.Media
import dev.klarkengkoy.triptrack.ui.navigation.NavigationState
import dev.klarkengkoy.triptrack.ui.navigation.Navigator
import dev.klarkengkoy.triptrack.ui.navigation.Settings
import dev.klarkengkoy.triptrack.ui.navigation.Trips
import dev.klarkengkoy.triptrack.ui.navigation.rememberNavigationState
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
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val isSignedIn by loginViewModel.isUserSignedIn.collectAsStateWithLifecycle()
    val mainUiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val activeTripUiState by mainViewModel.activeTripUiState.collectAsStateWithLifecycle()

    when (isSignedIn) {
        true -> {
            // Initialize Navigation State for the Main Graph
            val mainNavigationState = rememberNavigationState(
                startRoute = Trips(null),
                topLevelRoutes = setOf(Trips(null), Dashboard, Media, Maps, Settings)
            )
            val mainNavigator = remember { Navigator(mainNavigationState) }

            MainScreen(
                snackbarHostState = snackbarHostState,
                navigationState = mainNavigationState,
                navigator = mainNavigator,
                mainUiState = mainUiState,
                activeTripUiState = activeTripUiState,
                setTopAppBar = { mainViewModel.setTopAppBarState(it.title, it.navigationIcon, it.actions, it.isCenterAligned) }
            )
        }
        false -> {
            LoginNavigation(viewModel = loginViewModel)
        }
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(128.dp))
            }
        }
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
    navigationState: NavigationState,
    navigator: Navigator,
    mainUiState: MainUiState,
    activeTripUiState: ActiveTripUiState,
    setTopAppBar: (TopAppBarState) -> Unit
) {
    val bottomNavItems = listOf(
        BottomNavItem(Trips(null), R.drawable.travel_luggage_and_bags_24px, "Trips"),
        BottomNavItem(Dashboard, R.drawable.dashboard_24px, "Dashboard", isEnabled = activeTripUiState.hasActiveTrip),
        BottomNavItem(Media, R.drawable.photo_album_24px, "Media", isEnabled = activeTripUiState.hasActiveTrip),
        BottomNavItem(Maps, R.drawable.map_24px, "Maps", isEnabled = activeTripUiState.hasActiveTrip),
        BottomNavItem(Settings, R.drawable.settings_24px, "Settings")
    )

    val isPreview = LocalInspectionMode.current
    // Check if we are on a top level screen by checking if the current back stack has only 1 item
    // AND the current top level route matches the active stack.
    // A simpler heuristic for BottomNav visibility in Nav3 with these wizards:
    // If the top of the current stack is one of the Tab keys, show it.
    val currentStack = navigationState.backStacks[navigationState.topLevelRoute]
    val currentKey = currentStack?.lastOrNull()
    
    val shouldShowBottomNav = if (isPreview) {
        true
    } else {
        // Show bottom nav if the current screen is one of the top-level destinations.
        // Note: Trips(null) equality works because it's a data class.
        currentKey in setOf(Trips(null), Dashboard, Media, Maps, Settings)
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
            Box {
                AnimatedVisibility(
                    visible = shouldShowBottomNav,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    BottomNavigationBar(
                        navigator = navigator,
                        currentRoute = navigationState.topLevelRoute,
                        items = bottomNavItems
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isPreview) {
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
                navigationState = navigationState,
                navigator = navigator,
//                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding,
                setTopAppBar = setTopAppBar
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    TripTrackTheme {
        // Preview requires dummy state, which is hard with internal classes.
        // We can just show the Loading state or Mock objects if we had them.
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Preview Placeholder")
        }
    }
}
