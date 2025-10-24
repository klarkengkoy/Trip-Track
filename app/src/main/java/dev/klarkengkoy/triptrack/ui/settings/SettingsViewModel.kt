package dev.klarkengkoy.triptrack.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.klarkengkoy.triptrack.data.UserDataStore
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataStore: UserDataStore
) : ViewModel() {

    val userNameFlow = userDataStore.userNameFlow
    val userEmailFlow = userDataStore.userEmailFlow

    fun logout() {
        viewModelScope.launch {
            userDataStore.clear()
            Firebase.auth.signOut()
        }
    }

    fun deleteAccount(context: Context) {
        val user = Firebase.auth.currentUser!!
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Account deleted.", Toast.LENGTH_SHORT).show()
                    logout()
                } else {
                    Toast.makeText(context, "Failed to delete account.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
