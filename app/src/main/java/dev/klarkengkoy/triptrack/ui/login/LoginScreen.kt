package dev.klarkengkoy.triptrack.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.klarkengkoy.triptrack.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginScreen(
    isLoading: Boolean,
    signInProviders: List<SignInProvider>,
    onSignInClick: (SignInType) -> Unit,
    onToggleTheme: () -> Unit,
    onLegalClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
    ) {
        if (isLoading) {
             LoadingIndicator(modifier = Modifier.size(128.dp).align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                signInProviders.forEach { provider ->
                    SignInButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSignInClick(provider.type) },
                        text = provider.text,
                        backgroundColor = provider.backgroundColor,
                        contentColor = provider.contentColor,
                        icon = provider.icon
                    )
                }
            }
            LegalText(onLegalClick = onLegalClick, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp))

            FloatingActionButton(
                onClick = onToggleTheme,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Palette, contentDescription = stringResource(id = R.string.toggle_theme_content_description))
            }
        }
    }
}

@Composable
fun LegalText(onLegalClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
            append("By continuing, you agree to our ")
            val tos = "Terms of Service"
            pushLink(LinkAnnotation.Clickable(tag = tos, linkInteractionListener = { onLegalClick(tos) }))
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(tos)
            }
            pop()
            append(" and ")
            val pp = "Privacy Policy"
            pushLink(LinkAnnotation.Clickable(tag = pp, linkInteractionListener = { onLegalClick(pp) }))
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(pp)
            }
            pop()
            append(".")
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    icon: @Composable () -> Unit
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            icon()
            Text(
                text = text,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
