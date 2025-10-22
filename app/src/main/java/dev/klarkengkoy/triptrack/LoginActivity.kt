package dev.klarkengkoy.triptrack

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.klarkengkoy.triptrack.ui.login.LoginViewModel
import dev.klarkengkoy.triptrack.ui.login.SignInEvent
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        Log.d(TAG, "Sign-in result received")
        viewModel.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            )
        )
        supportActionBar?.hide()
        Log.d(TAG, "onCreate: LoginActivity started")

        if (FirebaseAuth.getInstance().currentUser != null) {
            Log.d(TAG, "User already signed in, navigating to main.")
            navigateToMain()
            return
        }

        Log.d(TAG, "User not signed in, setting up UI and event collection.")
        lifecycleScope.launch {
            viewModel.signInEvent.collect { event ->
                Log.d(TAG, "Collected sign-in event: $event")
                when (event) {
                    is SignInEvent.Launch -> launchSignIn(event.providers)
                    SignInEvent.Success -> {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            Log.d(TAG, "Sign-in success for user: ${user.displayName}")
                            val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("user_name", user.displayName)
                                putString("user_email", user.email)
                                apply()
                            }
                            Toast.makeText(this@LoginActivity, R.string.sign_in_successful, Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        } else {
                            Log.w(TAG, "Sign-in success, but current user is null.")
                        }
                    }
                    SignInEvent.Error -> {
                        Log.e(TAG, "Sign-in event error.")
                        Toast.makeText(this@LoginActivity, R.string.sign_in_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        setContent {
            LoginNavigation(viewModel)
        }
    }

    private fun launchSignIn(providers: List<AuthUI.IdpConfig>) {
        Log.d(TAG, "Launching sign-in flow with providers: $providers")
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun navigateToMain() {
        Log.d(TAG, "Navigating to MainActivity.")
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
