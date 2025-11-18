package dev.klarkengkoy.triptrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint
import dev.klarkengkoy.triptrack.ui.MainViewModel
import dev.klarkengkoy.triptrack.ui.TripTrackScreen
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.login.SignInEvent
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        Log.d(TAG, "Sign-in result received")
        loginViewModel.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            TripTrackTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val navController = rememberNavController()

                LaunchedEffect(Unit) {
                    loginViewModel.signInEvent.collect { event ->
                        when (event) {
                            is SignInEvent.Launch -> launchSignIn(event.providers)
                            is SignInEvent.Success -> {
                                snackbarHostState.showSnackbar(event.message)
                            }

                            is SignInEvent.Error -> {
                                snackbarHostState.showSnackbar(event.message)
                            }
                        }
                    }
                }

                TripTrackScreen(
                    loginViewModel = loginViewModel,
                    snackbarHostState = snackbarHostState,
                    navController = navController
                )
            }
        }
    }

    private fun launchSignIn(providers: List<AuthUI.IdpConfig>) {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
