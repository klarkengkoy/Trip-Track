package dev.klarkengkoy.triptrack.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class SignInType {
    GOOGLE,
    FACEBOOK,
    X,
    EMAIL,
    PHONE,
    ANONYMOUS
}

data class SignInProvider(
    val type: SignInType,
    val text: String,
    val icon: @Composable () -> Unit,
    val backgroundColor: Color,
    val contentColor: Color = Color.White
)
