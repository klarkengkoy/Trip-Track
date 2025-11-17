package dev.klarkengkoy.triptrack.ui.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun MapsScreen(
    mapsViewModel: MapsViewModel = hiltViewModel()
) {
    // TODO(klarkengkoy): We will also have a Maps Bottom Navigation would show Map with markers on where we have transactions.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Maps Screen")
    }
}
