package dev.klarkengkoy.triptrack.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.klarkengkoy.triptrack.R

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onToggleTheme: () -> Unit
) {
    val text by homeViewModel.text.observeAsState("")
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.Center)
        )
        FloatingActionButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Palette, contentDescription = stringResource(id = R.string.toggle_theme_content_description))
        }
    }
}
