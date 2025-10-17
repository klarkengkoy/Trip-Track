package dev.klarkengkoy.triptrack.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.edit
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.klarkengkoy.triptrack.MainActivity
import dev.klarkengkoy.triptrack.R
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = Firebase.auth
        credentialManager = CredentialManager.create(requireActivity())

        return ComposeView(requireContext()).apply {
            setContent {
                LoginScreen(
                    onGoogleSignInClick = { signIn() },
                    onDevLoginClick = {
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                )
            }
        }
    }

    private fun signIn() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(requireActivity(), request)
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    firebaseAuthWithGoogle(credential.idToken)
                } else {
                    Log.e(TAG, "Unexpected credential type")
                    Toast.makeText(requireContext(), "Sign-in failed: Unexpected credential type", Toast.LENGTH_LONG).show()
                }
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Sign-in failed", e)
                Toast.makeText(requireContext(), "Sign-in failed. Check app configuration.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user?.displayName, user?.email)
                } else {
                    Toast.makeText(requireContext(), "Firebase Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(displayName: String?, email: String?) {
        if (displayName != null && email != null) {
            val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPref.edit {
                putString("user_name", displayName)
                putString("user_email", email)
            }

            Toast.makeText(requireContext(), "Sign-in successful", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}

@Composable
fun LoginScreen(onGoogleSignInClick: () -> Unit, onDevLoginClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onGoogleSignInClick) {
            Text("Sign in with Google")
        }
        Button(onClick = onDevLoginClick) {
            Text("Continue (Dev)")
        }
    }
}
