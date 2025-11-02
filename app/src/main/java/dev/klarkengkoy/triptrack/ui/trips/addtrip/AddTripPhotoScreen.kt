package dev.klarkengkoy.triptrack.ui.trips.addtrip

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.AddTripUiState
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripPhotoScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    onNavigateNext: () -> Unit,
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        AddTripPhotoContent(
            modifier = Modifier.padding(paddingValues),
            addTripUiState = uiState.addTripUiState,
            onImageUriChanged = { viewModel.onImageUriChanged(it?.toString()) },
            onImageOffsetChanged = { x, y -> viewModel.onImageOffsetChanged(x, y) },
            onNextClicked = onNavigateNext,
            onSkipClicked = {
                viewModel.onImageUriChanged(null) // Clear the image
                onNavigateNext()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTripPhotoContent(
    modifier: Modifier = Modifier,
    addTripUiState: AddTripUiState,
    onImageUriChanged: (Uri?) -> Unit,
    onImageOffsetChanged: (Float, Float) -> Unit,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = onImageUriChanged
    )

    var offset by remember(addTripUiState.imageOffsetX, addTripUiState.imageOffsetY) {
        mutableStateOf(Offset(addTripUiState.imageOffsetX, addTripUiState.imageOffsetY))
    }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Position your cover photo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            ElevatedCard(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.large)
                        .onSizeChanged { containerSize = it },
                    contentAlignment = Alignment.Center
                ) {
                    if (addTripUiState.imageUri != null) {
                        AsyncImage(
                            model = addTripUiState.imageUri,
                            contentDescription = "Cover photo",
                            onSuccess = { success ->
                                imageSize = IntSize(
                                    width = success.painter.intrinsicSize.width.roundToInt(),
                                    height = success.painter.intrinsicSize.height.roundToInt()
                                )
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(imageSize, containerSize) {
                                    detectDragGestures(
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            val newOffset = offset + dragAmount

                                            val imageIntrinsicWidth = imageSize.width.toFloat()
                                            val imageIntrinsicHeight = imageSize.height.toFloat()

                                            if (imageIntrinsicWidth == 0f || imageIntrinsicHeight == 0f || containerSize.width == 0 || containerSize.height == 0) {
                                                offset = newOffset
                                                return@detectDragGestures
                                            }

                                            val containerAspectRatio = containerSize.width / containerSize.height.toFloat()
                                            val imageAspectRatio = imageIntrinsicWidth / imageIntrinsicHeight

                                            val scale = if (imageAspectRatio > containerAspectRatio) {
                                                containerSize.height / imageIntrinsicHeight
                                            } else {
                                                containerSize.width / imageIntrinsicWidth
                                            }

                                            val scaledImageWidth = imageIntrinsicWidth * scale
                                            val scaledImageHeight = imageIntrinsicHeight * scale

                                            val maxX = (scaledImageWidth - containerSize.width).coerceAtLeast(0f) / 2f
                                            val maxY = (scaledImageHeight - containerSize.height).coerceAtLeast(0f) / 2f

                                            offset = Offset(
                                                x = newOffset.x.coerceIn(-maxX, maxX),
                                                y = newOffset.y.coerceIn(-maxY, maxY)
                                            )
                                        },
                                        onDragEnd = {
                                            onImageOffsetChanged(offset.x, offset.y)
                                        }
                                    )
                                }
                                .graphicsLayer {
                                    translationX = offset.x
                                    translationY = offset.y
                                },
                            contentScale = ContentScale.Crop
                        )
                        // Scrim for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
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
                            val dateRange = if (addTripUiState.startDate != null && addTripUiState.endDate != null) {
                                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                val start = Instant.ofEpochMilli(addTripUiState.startDate).atZone(ZoneId.systemDefault()).toLocalDate()
                                val end = Instant.ofEpochMilli(addTripUiState.endDate).atZone(ZoneId.systemDefault()).toLocalDate()
                                "${start.format(formatter)} - ${end.format(formatter)}"
                            } else {
                                "No dates set"
                            }

                            Text(
                                text = addTripUiState.tripName, style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dateRange,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add a photo",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
            addTripUiState = AddTripUiState(
                tripName = "Trip to Japan",
                startDate = Instant.now().toEpochMilli(),
                endDate = Instant.now().plusMillis(1000 * 60 * 60 * 24 * 7).toEpochMilli(),
                imageUri = null
            ),
            onImageUriChanged = {},
            onImageOffsetChanged = { _, _ -> },
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
            addTripUiState = AddTripUiState(
                tripName = "Trip to Japan",
                startDate = Instant.now().toEpochMilli(),
                endDate = Instant.now().plusMillis(1000 * 60 * 60 * 24 * 7).toEpochMilli(),
                imageUri = "https://images.unsplash.com/photo-1542051841857-5f90071e7989?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
            ),
            onImageUriChanged = {},
            onImageOffsetChanged = { _, _ -> },
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}
