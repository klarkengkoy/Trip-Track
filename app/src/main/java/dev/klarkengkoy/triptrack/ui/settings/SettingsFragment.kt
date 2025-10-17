package dev.klarkengkoy.triptrack.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.klarkengkoy.triptrack.LoginActivity

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen(
                    onLogoutClick = { logout() },
                    onDeleteAccountClick = { deleteAccount() }
                )
            }
        }
    }

    private fun logout() {
        Firebase.auth.signOut()
        // Clear user info
        val sharedPref = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        // Navigate to LoginActivity
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun deleteAccount() {
        val user = Firebase.auth.currentUser!!
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Account deleted.", Toast.LENGTH_SHORT).show()
                    logout()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete account.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun SettingsScreen(onLogoutClick: () -> Unit, onDeleteAccountClick: () -> Unit) {
    val showDeleteDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userName = sharedPref.getString("user_name", "")
    val userEmail = sharedPref.getString("user_email", "")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User Name: $userName")
        Text(text = "User Email: $userEmail")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onLogoutClick) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showDeleteDialog.value = true }) {
            Text("Delete Account")
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        onDeleteAccountClick()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
