package dev.klarkengkoy.triptrack

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.klarkengkoy.triptrack.ui.login.LegalScreen
import dev.klarkengkoy.triptrack.ui.login.LoginScreen
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@Composable
fun LoginNavigation() {
    val navController = rememberNavController()
    var useDarkTheme by remember { mutableStateOf(false) }

    TripTrackTheme(useDarkTheme = useDarkTheme) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                val viewModel: LoginViewModel = hiltViewModel()
                val isLoading by viewModel.isLoading.collectAsState()
                LoginScreen(
                    isLoading = isLoading,
                    onGoogleSignInClick = { viewModel.onSignInRequested(viewModel.getGoogleProvider()) },
                    onAnonymousSignInClick = { viewModel.onSignInRequested(viewModel.getAnonymousProvider()) },
                    onEmailSignInClick = { viewModel.onSignInRequested(viewModel.getEmailProvider()) },
                    onPhoneSignInClick = { viewModel.onSignInRequested(viewModel.getPhoneProvider()) },
                    onFacebookSignInClick = { viewModel.onSignInRequested(viewModel.getFacebookProvider()) },
                    onXSignInClick = { viewModel.onSignInRequested(viewModel.getXProvider()) },
                    onToggleTheme = { useDarkTheme = !useDarkTheme },
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
