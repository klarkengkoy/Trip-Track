package dev.klarkengkoy.triptrack.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import dev.klarkengkoy.triptrack.R

class LegalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val clickedText = requireArguments().getString("clicked_text") ?: ""
        return ComposeView(requireContext()).apply {
            setContent {
                LegalScreen(clickedText = clickedText)
            }
        }
    }
}

@Composable
fun LegalScreen(clickedText: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        when (clickedText) {
            "Terms of Service" -> Text(stringResource(id = R.string.terms_and_conditions))
            "Privacy Policy" -> Text(stringResource(id = R.string.privacy_policy))
        }
    }
}
