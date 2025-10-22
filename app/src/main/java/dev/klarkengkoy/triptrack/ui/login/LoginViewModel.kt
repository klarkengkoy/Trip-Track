package dev.klarkengkoy.triptrack.ui.login

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.klarkengkoy.triptrack.R
import javax.inject.Inject
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

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signInEvent = MutableSharedFlow<SignInEvent>()
    val signInEvent = _signInEvent.asSharedFlow()

    init {
        Log.d(TAG, "LoginViewModel initialized")
    }

    val signInProviders = listOf(
        SignInProvider(
            type = SignInType.GOOGLE,
            text = "Sign in with Google",
            icon = { Icon(painter = painterResource(id = R.drawable.ic_google_logo), contentDescription = "Google", modifier = Modifier.size(18.dp), tint = Color.Unspecified) },
            backgroundColor = Color.White,
            contentColor = Color.Black
        ),
        SignInProvider(
            type = SignInType.FACEBOOK,
            text = "Sign in with Facebook",
            icon = { Icon(painter = painterResource(id = R.drawable.ic_facebook_logo), contentDescription = "Facebook", modifier = Modifier.size(18.dp), tint = Color.Unspecified) },
            backgroundColor = Color(0xFF1877F2)
        ),
        SignInProvider(
            type = SignInType.X,
            text = "Sign in with X",
            icon = { Icon(painter = painterResource(id = R.drawable.ic_x_logo), contentDescription = "X", modifier = Modifier.size(18.dp)) },
            backgroundColor = Color.Black
        ),
        SignInProvider(
            type = SignInType.EMAIL,
            text = "Sign in with Email",
            icon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(18.dp)) },
            backgroundColor = Color(0xFF7C4DFF)
        ),
        SignInProvider(
            type = SignInType.PHONE,
            text = "Sign in with Phone",
            icon = { Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", modifier = Modifier.size(18.dp)) },
            backgroundColor = Color(0xFF4CAF50)
        ),
        SignInProvider(
            type = SignInType.ANONYMOUS,
            text = "Sign in anonymously",
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Anonymous", modifier = Modifier.size(18.dp)) },
            backgroundColor = Color.Gray
        )
    )

    fun onSignInRequested(signInType: SignInType) {
        Log.d(TAG, "onSignInRequested for type: $signInType")
        val provider = when (signInType) {
            SignInType.GOOGLE -> AuthUI.IdpConfig.GoogleBuilder().build()
            SignInType.FACEBOOK -> AuthUI.IdpConfig.FacebookBuilder().build()
            SignInType.X -> AuthUI.IdpConfig.TwitterBuilder().build()
            SignInType.EMAIL -> AuthUI.IdpConfig.EmailBuilder().build()
            SignInType.PHONE -> AuthUI.IdpConfig.PhoneBuilder().build()
            SignInType.ANONYMOUS -> AuthUI.IdpConfig.AnonymousBuilder().build()
        }
        viewModelScope.launch {
            Log.d(TAG, "Emitting Launch event to UI")
            _isLoading.value = true
            _signInEvent.emit(SignInEvent.Launch(listOf(provider)))
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        Log.d(TAG, "onSignInResult received with code: ${result.resultCode}")
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModelScope.launch {
                Log.d(TAG, "Emitting Success event to UI")
                _signInEvent.emit(SignInEvent.Success)
            }
        } else {
            Log.w(TAG, "Sign-in failed or was canceled by user.")
            _isLoading.value = false
            viewModelScope.launch {
                Log.d(TAG, "Emitting Error event to UI")
                _signInEvent.emit(SignInEvent.Error)
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}