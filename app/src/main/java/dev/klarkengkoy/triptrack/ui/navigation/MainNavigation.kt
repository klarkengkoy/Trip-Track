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
import androidx.navigation.navArgument
import androidx.navigation.navigation
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

const val ADD_TRIP_ROUTE = "addTrip"
const val EDIT_TRIP_ROUTE = "editTrip"
const val ADD_TRANSACTION_ROUTE = "addTransaction"
const val EDIT_TRANSACTION_ROUTE = "editTransaction"

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
    setTopAppBar: (TopAppBarState) -> Unit
) {
    NavHost(navController, startDestination = "trips", modifier = modifier) {
        tripsGraph(navController, setTopAppBar)
        dashboardGraph()
        mediaGraph()
        mapsGraph()
        settingsGraph(setTopAppBar)
    }
}

private fun NavGraphBuilder.tripsGraph(
    navController: NavHostController,
    setTopAppBar: (TopAppBarState) -> Unit
) {
    composable(
        route = "trips?tripId={tripId}",
        arguments = listOf(navArgument("tripId") { nullable = true })
    ) {
        TripsScreen(
            setTopAppBar = setTopAppBar,
            onAddTrip = { navController.navigate(ADD_TRIP_ROUTE) },
            onAddTransaction = { tripId -> navController.navigate("$ADD_TRANSACTION_ROUTE/$tripId") },
            onEditTrip = { tripId -> navController.navigate("$EDIT_TRIP_ROUTE/$tripId") },
            onEditTransaction = { transactionId -> navController.navigate("$EDIT_TRANSACTION_ROUTE/$transactionId") }
        )
    }

    addTransactionGraph(navController, setTopAppBar)
    editTransactionGraph(navController, setTopAppBar)
    addTripGraph(navController, setTopAppBar)
    editTripGraph(navController, setTopAppBar)
}

private fun NavGraphBuilder.addTransactionGraph(
    navController: NavHostController,
    setTopAppBar: (TopAppBarState) -> Unit
) {
    navigation(startDestination = "addTransactionCategory", route = "$ADD_TRANSACTION_ROUTE/{tripId}") {
        composable("addTransactionCategory") { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Select a Category") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
            }

            CategoryScreen(onCategorySelected = { category ->
                navController.navigate("addTransactionDetails/$tripId/$category")
            })
        }

        composable("addTransactionDetails/{tripId}/{category}") { 
            val tripId = it.arguments?.getString("tripId") ?: ""
            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Transaction") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
            }
            TransactionScreen(
                onSave = {
                    navController.navigate("trips") {
                        popUpTo("$ADD_TRANSACTION_ROUTE/{tripId}") { inclusive = true }
                    }
                },
                onCategoryClick = {
                    navController.navigate("addTransactionCategory") {
                         popUpTo("addTransactionDetails/$tripId/{category}") { inclusive = true }
                    }
                }
            )
        }
    }
}

// Placeholder for Edit Transaction Graph - needs full implementation details similar to Add
private fun NavGraphBuilder.editTransactionGraph(
    navController: NavHostController,
    setTopAppBar: (TopAppBarState) -> Unit
) {
     // Basic placeholder to resolve compilation error for now. 
     // You will need to implement fetching the existing transaction and pre-filling the TransactionScreen.
     navigation(startDestination = "editTransactionDetails", route = "$EDIT_TRANSACTION_ROUTE/{transactionId}") {
         composable("editTransactionDetails") { backStackEntry ->
             val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
             
             LaunchedEffect(Unit) {
                 setTopAppBar(TopAppBarState(
                     title = { Text("Edit Transaction") },
                     navigationIcon = {
                         IconButton(onClick = { navController.navigateUp() }) {
                             Icon(
                                 imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                 contentDescription = stringResource(R.string.cd_navigate_up)
                             )
                         }
                     }
                 ))
             }
             
             TransactionScreen(
                 onSave = { navController.navigateUp() },
                 onCategoryClick = { 
                     navController.navigate("editTransactionCategory/$transactionId") 
                 }
             )
         }
         
         composable("editTransactionCategory/{transactionId}") { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""

            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Select a Category") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
            }

            CategoryScreen(onCategorySelected = { category ->
                // We navigate back to details, passing the NEW category as an argument.
                // NOTE: This requires TransactionViewModel to look for this argument when recomposing or re-initializing?
                // Or better, we just navigate back and let the ViewModel pick it up from the savedStateHandle if we passed it back.
                // A simpler approach for now: Navigate to a route that looks like the details route but with category param,
                // similar to Add flow. However, editTransactionDetails currently only takes {transactionId}.
                // We should probably update the editTransactionDetails route to optionally take a category override, 
                // or update the ViewModel to listen to a result. 
                
                // For simplicity and consistency with ADD flow, let's route back to the details screen
                // but we need a way to tell the ViewModel "Hey, use this new category".
                // The Add flow works because the route IS "addTransactionDetails/{tripId}/{category}".
                // Let's make the Edit flow work similarly if possible, or pass it back via SavedStateHandle.
                
                navController.previousBackStackEntry?.savedStateHandle?.set("category", category)
                navController.popBackStack()
            })
        }
     }
}

private fun NavGraphBuilder.addTripGraph(
    navController: NavHostController,
    setTopAppBar: (TopAppBarState) -> Unit
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Select Currency") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
    setTopAppBar: (TopAppBarState) -> Unit
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))

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
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Add Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
            val backStackEntry = remember(entry) { navController.getBackStackEntry("$EDIT_TRIP_ROUTE/{tripId}") }
            val viewModel: TripsViewModel = hiltViewModel(backStackEntry)

            LaunchedEffect(Unit) {
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Edit Trip") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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
                setTopAppBar(TopAppBarState(
                    title = { Text("Select Currency") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_navigate_up)
                            )
                        }
                    }
                ))
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

private fun NavGraphBuilder.settingsGraph(setTopAppBar: (TopAppBarState) -> Unit) {
    composable("settings") {
        SettingsScreen(setTopAppBar = setTopAppBar)
    }
}
