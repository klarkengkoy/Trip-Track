package dev.klarkengkoy.triptrack.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.klarkengkoy.triptrack.MainActivity
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var credentialManager: CredentialManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        credentialManager = CredentialManager.create(requireActivity())

        binding.signInButton.setOnClickListener {
            // signIn()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
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
                    updateUI(credential)
                } else {
                    Log.e(TAG, "Unexpected credential type")
                }
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Sign-in failed", e)
            }
        }
    }

    private fun updateUI(credential: GoogleIdTokenCredential?) {
        if (credential != null) {
            // Signed in successfully, you can get user information from the credential object
            // val id = credential.id
            // val displayName = credential.displayName
            // val email = credential.id // The ID is an email address if available
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
    */

    companion object {
        private const val TAG = "LoginFragment"
    }
}