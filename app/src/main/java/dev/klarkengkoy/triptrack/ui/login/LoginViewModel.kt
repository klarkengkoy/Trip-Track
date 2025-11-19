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
import dev.klarkengkoy.triptrack.data.UserDataStore
import dev.klarkengkoy.triptrack.data.repository.AuthRepository
import dev.klarkengkoy.triptrack.data.repository.TripsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignInEvent {
    data class Launch(val providers: List<AuthUI.IdpConfig>) : SignInEvent()
    data class Success(val message: String) : SignInEvent()
    data class Error(val message: String) : SignInEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val tripsRepository: TripsRepository,
    private val authRepository: AuthRepository,
    private val signInProviderFactory: SignInProviderFactory
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _signInEvent = MutableSharedFlow<SignInEvent>()
    val signInEvent = _signInEvent.asSharedFlow()

    // Changed from StateFlow<Boolean> to StateFlow<Boolean?> to support "Loading" state
    val isUserSignedIn: StateFlow<Boolean?> = authRepository.isUserSignedIn
        .map { it as Boolean? } // Explicit cast for clarity, though mostly redundant
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Null means "checking auth state"
        )

    init {
        viewModelScope.launch {
            isUserSignedIn.collect { isSignedIn ->
                _isLoading.value = false
                if (isSignedIn == true) {
                    tripsRepository.syncTrips()
                }
            }
        }
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
            icon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Email", modifier = Modifier.size(18.dp)) },
            backgroundColor = Color(0xFF7C4DFF)
        ),
        SignInProvider(
            type = SignInType.PHONE,
            text = "Sign in with Phone",
            icon = { Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone", modifier = Modifier.size(18.dp)) },
            backgroundColor = Color(0xFF4CAF50)
        ),
        SignInProvider(
            type = SignInType.ANONYMOUS,
            text = "Sign in anonymously",
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Anonymous", modifier = Modifier.size(18.dp)) },
            backgroundColor = Color.Gray
        )
    )

    fun onSignInRequested(signInType: SignInType) {
        val provider = signInProviderFactory.create(signInType)
        viewModelScope.launch {
            _isLoading.value = true
            _signInEvent.emit(SignInEvent.Launch(listOf(provider)))
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModelScope.launch {
                val name = authRepository.getUserName()
                val email = authRepository.getUserEmail()
                if (name != null && email != null) {
                    userDataStore.saveUser(name, email)
                }
                val message = if (name != null) "Welcome, $name" else "Sign-in successful"
                _signInEvent.emit(SignInEvent.Success(message))
            }
        } else {
            viewModelScope.launch {
                _isLoading.value = false
                _signInEvent.emit(SignInEvent.Error("Sign-in failed"))
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
