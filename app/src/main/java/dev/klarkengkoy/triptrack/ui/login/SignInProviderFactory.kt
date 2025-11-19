package dev.klarkengkoy.triptrack.ui.login

import com.firebase.ui.auth.AuthUI
import javax.inject.Inject

interface SignInProviderFactory {
    fun create(signInType: SignInType): AuthUI.IdpConfig
}

class SignInProviderFactoryImpl @Inject constructor() : SignInProviderFactory {
    override fun create(signInType: SignInType): AuthUI.IdpConfig {
        return when (signInType) {
            SignInType.GOOGLE -> AuthUI.IdpConfig.GoogleBuilder().build()
            SignInType.FACEBOOK -> AuthUI.IdpConfig.FacebookBuilder().build()
            SignInType.X -> AuthUI.IdpConfig.TwitterBuilder().build()
            SignInType.EMAIL -> AuthUI.IdpConfig.EmailBuilder().build()
            SignInType.PHONE -> AuthUI.IdpConfig.PhoneBuilder().build()
            SignInType.ANONYMOUS -> AuthUI.IdpConfig.AnonymousBuilder().build()
        }
    }
}
