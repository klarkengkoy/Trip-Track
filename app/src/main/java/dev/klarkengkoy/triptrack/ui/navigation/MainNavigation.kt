package dev.klarkengkoy.triptrack.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
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
import dev.klarkengkoy.triptrack.ui.trips.transaction.TransactionScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.CurrencyListScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripBudgetScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripCurrencyScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripDatesScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripNameScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripPhotoScreen
import dev.klarkengkoy.triptrack.ui.trips.tripdetails.TripSummaryScreen

const val ADD_TRIP_ROUTE = "addTrip"
const val EDIT_TRIP_ROUTE = "editTrip"
private const val ANIMATION_DURATION = 700

private fun slideIn(
    direction: AnimatedContentTransitionScope.SlideDirection
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideIntoContainer(direction, animationSpec = tween(ANIMATION_DURATION))
}

private fun slideOut(
    direction: AnimatedContentTransitionScope.SlideDirection
): AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutOfContainer(direction, animationSpec = tween(ANIMATION_DURATION))
}

private val slideInFromLeft = slideIn(AnimatedContentTransitionScope.SlideDirection.Left)
private val slideOutToLeft = slideOut(AnimatedContentTransitionScope.SlideDirection.Left)
private val slideInFromRight = slideIn(AnimatedContentTransitionScope.SlideDirection.Right)
private val slideOutToRight = slideOut(AnimatedContentTransitionScope.SlideDirection.Right)
private val slideInFromUp = slideIn(AnimatedContentTransitionScope.SlideDirection.Up)
private val slideOutToDown = slideOut(AnimatedContentTransitionScope.SlideDirection.Down)
private val slideInFromDown = slideIn(AnimatedContentTransitionScope.SlideDirection.Down)
private val slideOutToUp = slideOut(AnimatedContentTransitionScope.SlideDirection.Up)

private val fadeInAnim: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(animationSpec = tween(ANIMATION_DURATION))
}

private val fadeOutAnim: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(animationSpec = tween(ANIMATION_DURATION))
}

@Composable
fun MainNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel
) {
    NavHost(navController, startDestination = "trips", modifier = modifier) {
        tripsGraph(navController, mainViewModel)
        dashboardGraph()
        mediaGraph()
        mapsGraph()
        settingsGraph(mainViewModel)
    }
}

private fun NavGraphBuilder.tripsGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    composable("trips") {
        TripsScreen(
            mainViewModel = mainViewModel,
            onAddTrip = { navController.navigate(ADD_TRIP_ROUTE) },
            onAddTransaction = { tripId -> navController.navigate("addTransaction/$tripId") },
            onEditTrip = { tripId -> navController.navigate("$EDIT_TRIP_ROUTE/$tripId") }
        )
    }

    composable("addTransaction/{tripId}") {
        TransactionScreen()
    }

    addTripGraph(navController, mainViewModel)
    editTripGraph(navController, mainViewModel)
}

private fun NavGraphBuilder.addTripGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    navigation(startDestination = "addTripName", route = ADD_TRIP_ROUTE) {
        composable(
            "addTripName",
            enterTransition = slideInFromUp,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToDown
        ) { entry ->
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

            TripNameScreen(
                onNavigateNext = { navController.navigate("addTripCurrency") },
                viewModel = viewModel
            )
        }
        composable(
            "addTripCurrency",
            enterTransition = slideInFromLeft,
            exitTransition = {
                when (targetState.destination.route) {
                    "addCurrencyList" -> fadeOutAnim()
                    else -> slideOutToLeft()
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "addCurrencyList" -> fadeInAnim()
                    else -> slideInFromRight()
                }
            },
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripCurrencyScreen(
                onNavigateNext = { navController.navigate("addTripDates") },
                onCurrencyClick = { navController.navigate("addCurrencyList") },
                viewModel = viewModel
            )
        }
        composable(
            "addTripDates",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripDatesScreen(
                onNavigateNext = { navController.navigate("addTripBudget") },
                viewModel = viewModel
            )
        }
        composable(
            "addTripBudget",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripBudgetScreen(
                onNavigateNext = { navController.navigate("addTripPhoto") },
                viewModel = viewModel
            )
        }
        composable(
            "addTripPhoto",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripPhotoScreen(
                onNavigateNext = { navController.navigate("addTripSummary") },
                viewModel = viewModel
            )
        }
        composable(
            "addTripSummary",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripSummaryScreen(
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
        composable(
            "addCurrencyList",
            enterTransition = slideInFromDown,
            popExitTransition = slideOutToUp
        ) { entry ->
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
}

private fun NavGraphBuilder.editTripGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    navigation(startDestination = "editTripName", route = "$EDIT_TRIP_ROUTE/{tripId}") {
        composable(
            "editTripName",
            enterTransition = slideInFromUp,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToDown
        ) { entry ->
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

            TripNameScreen(
                onNavigateNext = { navController.navigate("editTripCurrency") },
                viewModel = viewModel
            )
        }
        composable(
            "editTripCurrency",
            enterTransition = slideInFromLeft,
            exitTransition = {
                when (targetState.destination.route) {
                    "editCurrencyList" -> fadeOutAnim()
                    else -> slideOutToLeft()
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    "editCurrencyList" -> fadeInAnim()
                    else -> slideInFromRight()
                }
            },
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripCurrencyScreen(
                onNavigateNext = { navController.navigate("editTripDates") },
                onCurrencyClick = { navController.navigate("editCurrencyList") },
                viewModel = viewModel
            )
        }
        composable(
            "editTripDates",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripDatesScreen(
                onNavigateNext = { navController.navigate("editTripBudget") },
                viewModel = viewModel
            )
        }
        composable(
            "editTripBudget",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripBudgetScreen(
                onNavigateNext = { navController.navigate("editTripPhoto") },
                viewModel = viewModel
            )
        }
        composable(
            "editTripPhoto",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripPhotoScreen(
                onNavigateNext = { navController.navigate("editTripSummary") },
                viewModel = viewModel
            )
        }
        composable(
            "editTripSummary",
            enterTransition = slideInFromLeft,
            exitTransition = slideOutToLeft,
            popEnterTransition = slideInFromRight,
            popExitTransition = slideOutToRight
        ) { entry ->
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

            TripSummaryScreen(
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
        composable(
            "editCurrencyList",
            enterTransition = slideInFromDown,
            popExitTransition = slideOutToUp
        ) { entry ->
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
}

private fun NavGraphBuilder.dashboardGraph() {
    composable("dashboard") {
        DashboardScreen()
    }
}

private fun NavGraphBuilder.mediaGraph() {
    composable("media") {
        MediaScreen()
    }
}

private fun NavGraphBuilder.mapsGraph() {
    composable("maps") {
        MapsScreen()
    }
}

private fun NavGraphBuilder.settingsGraph(mainViewModel: MainViewModel) {
    composable("settings") {
        SettingsScreen(mainViewModel = mainViewModel)
    }
}
