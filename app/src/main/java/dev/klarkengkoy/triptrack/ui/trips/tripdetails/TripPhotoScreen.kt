package dev.klarkengkoy.triptrack.ui.trips.tripdetails

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripUiState
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun TripPhotoScreen(
    modifier: Modifier = Modifier,
    onNavigateNext: () -> Unit,
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var scale by remember(uiState.tripUiState.imageScale) { mutableFloatStateOf(uiState.tripUiState.imageScale) }
    var offset by remember(uiState.tripUiState.imageOffsetX, uiState.tripUiState.imageOffsetY) {
        mutableStateOf(Offset(uiState.tripUiState.imageOffsetX, uiState.tripUiState.imageOffsetY))
    }

    fun saveState() {
        viewModel.onImageOffsetChanged(offset.x, offset.y)
        viewModel.onImageScaleChanged(scale)
    }

    // Save the state when the user leaves the screen
    DisposableEffect(Unit) {
        onDispose {
            saveState()
        }
    }

    AddTripPhotoContent(
        modifier = modifier,
        tripUiState = uiState.tripUiState,
        scale = scale,
        offset = offset,
        onTransform = { pan, zoom ->
            scale = (scale * zoom).coerceIn(1f, 5f)
            offset += pan
        },
        onImageUriChanged = { uri ->
            viewModel.onImageUriChanged(uri?.toString())
            // Reset state when a new image is picked
            scale = 1f
            offset = Offset.Zero
        },
        onNextClicked = {
            saveState()
            onNavigateNext()
        },
        onSkipClicked = {
            viewModel.onImageUriChanged(null) // Clear the image
            onNavigateNext()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTripPhotoContent(
    modifier: Modifier = Modifier,
    tripUiState: TripUiState,
    scale: Float,
    offset: Offset,
    onTransform: (pan: Offset, zoom: Float) -> Unit,
    onImageUriChanged: (Uri?) -> Unit,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
                onImageUriChanged(it)
            }
        }
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Position your cover photo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                onTransform(pan, zoom)
                            }
                        }
                ) {
                    if (tripUiState.imageUri != null) {
                        AsyncImage(
                            model = tripUiState.imageUri,
                            contentDescription = "Cover photo",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    translationX = offset.x
                                    translationY = offset.y
                                },
                            contentScale = ContentScale.Fit
                        )
                        // Scrim for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.scrim.copy(alpha = 0f),
                                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)
                                        ),
                                        startY = 300f
                                    )
                                )
                        )
                        // Content
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = tripUiState.tripName, style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (tripUiState.startDate != null && tripUiState.endDate != null) {
                                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                val start = Instant.ofEpochMilli(tripUiState.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
                                val end = Instant.ofEpochMilli(tripUiState.endDate).atZone(ZoneId.systemDefault()).toLocalDate()
                                val dateRange = "${start.format(formatter)} - ${end.format(formatter)}"

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateRange,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add a photo",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap to select an image",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onNextClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next")
            }
            TextButton(onClick = onSkipClicked) {
                Text("Skip for now")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTripPhotoScreenPreview() {
    TripTrackTheme {
        AddTripPhotoContent(
            tripUiState = TripUiState(
                tripName = "Trip to Japan",
                startDate = Instant.now().toEpochMilli(),
                endDate = Instant.now().plusMillis(1000 * 60 * 60 * 24 * 7).toEpochMilli(),
                imageUri = null
            ),
            scale = 1f,
            offset = Offset.Zero,
            onTransform = { _, _ -> },
            onImageUriChanged = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTripPhotoScreenPreview_WithImage() {
    TripTrackTheme {
        AddTripPhotoContent(
            tripUiState = TripUiState(
                tripName = "Trip to Japan",
                startDate = Instant.now().toEpochMilli(),
                endDate = Instant.now().plusMillis(1000 * 60 * 60 * 24 * 7).toEpochMilli(),
                imageUri = "https://images.unsplash.com/photo-1542051841857-5f90071e7989?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
            ),
            scale = 1f,
            offset = Offset.Zero,
            onTransform = { _, _ -> },
            onImageUriChanged = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}
