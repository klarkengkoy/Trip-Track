package dev.klarkengkoy.triptrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.klarkengkoy.triptrack.ui.login.LegalScreen
import dev.klarkengkoy.triptrack.ui.login.LoginScreen
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@Composable
fun LoginNavigation(viewModel: LoginViewModel) {
    val navController = rememberNavController()

    TripTrackTheme {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                val isLoading by viewModel.isLoading.collectAsState()
                LoginScreen(
                    isLoading = isLoading,
                    signInProviders = viewModel.signInProviders,
                    onSignInClick = { signInType -> viewModel.onSignInRequested(signInType) },
                    onLegalClick = { clickedText: String ->
                        navController.navigate("legal/$clickedText")
                    }
                )
            }
            composable("legal/{clicked_text}") { backStackEntry ->
                val clickedText = backStackEntry.arguments?.getString("clicked_text") ?: ""
                LegalScreen(
                    clickedText = clickedText,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
