package dev.klarkengkoy.triptrack.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignInEvent {
    data class Launch(val providers: List<AuthUI.IdpConfig>) : SignInEvent()
    object Success : SignInEvent()
    object Error : SignInEvent()
}

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signInEvent = MutableSharedFlow<SignInEvent>()
    val signInEvent = _signInEvent.asSharedFlow()

    fun onSignInRequested(provider: AuthUI.IdpConfig) {
        viewModelScope.launch {
            _isLoading.value = true
            _signInEvent.emit(SignInEvent.Launch(listOf(provider)))
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModelScope.launch {
                _signInEvent.emit(SignInEvent.Success)
            }
        } else {
            _isLoading.value = false
            viewModelScope.launch {
                _signInEvent.emit(SignInEvent.Error)
            }
        }
    }

    fun getGoogleProvider() = AuthUI.IdpConfig.GoogleBuilder().build()
    fun getAnonymousProvider() = AuthUI.IdpConfig.AnonymousBuilder().build()
    fun getFacebookProvider() = AuthUI.IdpConfig.FacebookBuilder().build()
    fun getTwitterProvider() = AuthUI.IdpConfig.TwitterBuilder().build()
    fun getEmailProvider() = AuthUI.IdpConfig.EmailBuilder().build()
    fun getPhoneProvider() = AuthUI.IdpConfig.PhoneBuilder().build()
}