package dev.klarkengkoy.triptrack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import dev.klarkengkoy.triptrack.ui.login.LegalScreen
import dev.klarkengkoy.triptrack.ui.login.LoginScreen
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@Composable
fun LoginNavigation(viewModel: LoginViewModel) {
    // 1. Initialize Navigation State for Navigation 3
    // Start with Login screen. We must include it in topLevelRoutes to initialize its back stack.
    val navigationState = rememberNavigationState(Login, setOf(Login))
    val navigator = remember { Navigator(navigationState) }

    TripTrackTheme {
        // 2. Create Entry Provider using DSL
        val entryProvider = entryProvider {
            entry<Login> {
                val isLoading by viewModel.isLoading.collectAsState()
                LoginScreen(
                    isLoading = isLoading,
                    signInProviders = viewModel.signInProviders,
                    onSignInClick = { signInType -> viewModel.onSignInRequested(signInType) },
                    onLegalClick = { clickedText ->
                        navigator.navigate(Legal(clickedText))
                    }
                )
            }
            entry<Legal> { key ->
                LegalScreen(
                    clickedText = key.clickedText,
                    onBack = { navigator.goBack() }
                )
            }
        }

        // 3. Use NavDisplay with NavigationState.toEntries to support state saving and back handling
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() }
        )
    }
}
