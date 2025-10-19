package dev.klarkengkoy.triptrack.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import dev.klarkengkoy.triptrack.MainActivity
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.ui.theme.LocalLoginScreenColors
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    private val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        viewModel.onSignInResult(res)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var useDarkTheme by remember { mutableStateOf(false) }
                TripTrackTheme(useDarkTheme = useDarkTheme) {
                    val isLoading by viewModel.isLoading.collectAsState()
                    LoginScreen(
                        isLoading = isLoading,
                        onGoogleSignInClick = { viewModel.onSignInRequested(viewModel.getGoogleProvider()) },
                        onAnonymousSignInClick = { viewModel.onSignInRequested(viewModel.getAnonymousProvider()) },
                        onEmailSignInClick = { viewModel.onSignInRequested(viewModel.getEmailProvider()) },
                        onPhoneSignInClick = { viewModel.onSignInRequested(viewModel.getPhoneProvider()) },
                        onFacebookSignInClick = { viewModel.onSignInRequested(viewModel.getFacebookProvider()) },
                        onTwitterSignInClick = { viewModel.onSignInRequested(viewModel.getTwitterProvider()) },
                        onToggleTheme = { useDarkTheme = !useDarkTheme },
                        onLegalClick = { clickedText ->
                            findNavController().navigate(R.id.action_login_to_legal, bundleOf("clicked_text" to clickedText))
                        }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToMain()
            return
        }

        lifecycleScope.launch {
            viewModel.signInEvent.collect { event ->
                when (event) {
                    is SignInEvent.Launch -> launchSignIn(event.providers)
                    SignInEvent.Success -> {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            sharedPref.edit {
                                putString("user_name", user.displayName)
                                putString("user_email", user.email)
                            }
                            Toast.makeText(requireContext(), R.string.sign_in_successful, Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        }
                    }
                    SignInEvent.Error -> {
                        Toast.makeText(requireContext(), R.string.sign_in_failed, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun launchSignIn(providers: List<AuthUI.IdpConfig>) {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_launcher_foreground)
            .setTheme(R.style.Theme_TripTrack)
            .setTosAndPrivacyPolicyUrls(
                "https://example.com/terms.html",
                "https://example.com/privacy.html",
            )
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun navigateToMain() {
        val intent = Intent(requireActivity(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginScreen(
    isLoading: Boolean,
    onGoogleSignInClick: () -> Unit,
    onAnonymousSignInClick: () -> Unit,
    onEmailSignInClick: () -> Unit,
    onPhoneSignInClick: () -> Unit,
    onFacebookSignInClick: () -> Unit,
    onTwitterSignInClick: () -> Unit,
    onToggleTheme: () -> Unit,
    onLegalClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
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
                val buttonModifier = Modifier.fillMaxWidth()
                val loginScreenColors = LocalLoginScreenColors.current

                SignInButton(
                    modifier = buttonModifier,
                    onClick = onGoogleSignInClick,
                    text = stringResource(id = R.string.sign_in_with_google),
                    backgroundColor = loginScreenColors.googleBackgroundColor,
                    contentColor = loginScreenColors.googleContentColor,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = stringResource(id = R.string.google_logo_content_description),
                            modifier = Modifier.size(18.dp),
                            tint = Color.Unspecified
                        )
                    }
                )
                SignInButton(
                    modifier = buttonModifier,
                    onClick = onFacebookSignInClick,
                    text = stringResource(id = R.string.sign_in_with_facebook),
                    backgroundColor = loginScreenColors.facebookBackgroundColor,
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(id = R.string.facebook_logo_content_description), modifier = Modifier.size(18.dp)) }
                )
                SignInButton(
                    modifier = buttonModifier,
                    onClick = onTwitterSignInClick,
                    text = stringResource(id = R.string.sign_in_with_twitter),
                    backgroundColor = loginScreenColors.twitterBackgroundColor,
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(id = R.string.twitter_logo_content_description), modifier = Modifier.size(18.dp)) }
                )
                SignInButton(
                    modifier = buttonModifier,
                    onClick = onEmailSignInClick,
                    text = stringResource(id = R.string.sign_in_with_email),
                    backgroundColor = loginScreenColors.emailBackgroundColor,
                    icon = { Icon(Icons.Default.Email, contentDescription = stringResource(id = R.string.email_sign_in_content_description), modifier = Modifier.size(18.dp)) }
                )
                SignInButton(
                    modifier = buttonModifier,
                    onClick = onPhoneSignInClick,
                    text = stringResource(id = R.string.sign_in_with_phone),
                    backgroundColor = loginScreenColors.phoneBackgroundColor,
                    icon = { Icon(Icons.Default.Phone, contentDescription = stringResource(id = R.string.phone_sign_in_content_description), modifier = Modifier.size(18.dp)) }
                )
                SignInButton(
                    modifier = buttonModifier,
                    onClick = onAnonymousSignInClick,
                    text = stringResource(id = R.string.sign_in_anonymously),
                    backgroundColor = loginScreenColors.anonymousBackgroundColor,
                    icon = { Icon(Icons.Default.Person, contentDescription = stringResource(id = R.string.anonymous_sign_in_content_description), modifier = Modifier.size(18.dp)) }
                )
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

@Composable
fun SignInButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    icon: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        icon()
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
