package dev.klarkengkoy.triptrack.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    fun logout(context: Context) {
        Firebase.auth.signOut()
        // Clear user info
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    fun deleteAccount(context: Context) {
        val user = Firebase.auth.currentUser!!
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Account deleted.", Toast.LENGTH_SHORT).show()
                    logout(context)
                } else {
                    Toast.makeText(context, "Failed to delete account.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
