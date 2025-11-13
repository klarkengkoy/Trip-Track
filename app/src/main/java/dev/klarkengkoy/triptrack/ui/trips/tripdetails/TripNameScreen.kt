package dev.klarkengkoy.triptrack.ui.trips.tripdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel

@Composable
fun TripNameScreen(
    modifier: Modifier = Modifier,
    onNavigateNext: () -> Unit = {},
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TripNameContent(
        modifier = modifier,
        name = uiState.tripUiState.tripName,
        onNameChanged = { viewModel.onTripNameChanged(it) },
        onNextClicked = onNavigateNext
    )
}

@Composable
private fun TripNameContent(
    modifier: Modifier = Modifier,
    name: String,
    onNameChanged: (String) -> Unit,
    onNextClicked: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Name your trip",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                BasicTextField(
                    value = name,
                    onValueChange = onNameChanged,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondaryContainer),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (name.isEmpty()) {
                                Text(
                                    text = "e.g. Seoul Searching or Q3 Getaway",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        Button(
            onClick = onNextClicked,
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text("Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripNameScreenPreview() {
    TripTrackTheme {
        TripNameContent(
            name = "",
            onNameChanged = {},
            onNextClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TripNameScreenPreview_WithName() {
    TripTrackTheme {
        TripNameContent(
            name = "Trip to Japan",
            onNameChanged = {},
            onNextClicked = {}
        )
    }
}
