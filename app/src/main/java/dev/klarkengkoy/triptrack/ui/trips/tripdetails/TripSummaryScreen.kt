package dev.klarkengkoy.triptrack.ui.trips.tripdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripUiState
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Currency

@Composable
fun TripSummaryScreen(
    onSaveTrip: () -> Unit,
    onDiscard: () -> Unit,
    viewModel: TripsViewModel,
    saveButtonText: String
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddTripSummaryContent(
        tripUiState = uiState.tripUiState,
        onSaveTrip = onSaveTrip,
        onDiscard = onDiscard,
        saveButtonText = saveButtonText
    )
}

@Composable
private fun AddTripSummaryContent(
    modifier: Modifier = Modifier,
    tripUiState: TripUiState,
    onSaveTrip: () -> Unit,
    onDiscard: () -> Unit,
    saveButtonText: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    val summaryItems = remember(tripUiState) {
        val currencySymbol = if (tripUiState.currency.isNotBlank()) {
            if (tripUiState.isCurrencyCustom) {
                tripUiState.currency
            } else {
                try {
                    Currency.getInstance(tripUiState.currency).symbol
                } catch (_: Exception) {
                    tripUiState.currency
                }
            }
        } else {
            ""
        }

        buildList {
            if (tripUiState.tripName.isNotBlank()) {
                add("Trip Name" to tripUiState.tripName)
            }
            if (tripUiState.currency.isNotBlank()) {
                val currencyLabel = if (tripUiState.isCurrencyCustom) {
                    tripUiState.currency
                } else {
                    try {
                        val currency = Currency.getInstance(tripUiState.currency)
                        "${currency.displayName} ($currencySymbol)"
                    } catch (_: Exception) {
                        tripUiState.currency // Fallback to code
                    }
                }
                add("Currency" to currencyLabel)
            }
            tripUiState.startDate?.let {
                val formatted = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                add("Start Date" to formatted)
            }
            tripUiState.endDate?.let {
                val formatted = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                add("End Date" to formatted)
            }
            tripUiState.totalBudget?.let {
                val numberFormat = NumberFormat.getNumberInstance()
                add("Total Budget" to "$currencySymbol ${numberFormat.format(it)}")
            }
            tripUiState.dailyBudget?.let {
                val numberFormat = NumberFormat.getNumberInstance()
                add("Daily Budget" to "$currencySymbol ${numberFormat.format(it)}")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Review your trip",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp),
                color = colorScheme.onSurface
            )

            if (tripUiState.imageUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = tripUiState.imageUri,
                            contentDescription = "Cover photo for ${tripUiState.tripName}",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = tripUiState.imageScale
                                    scaleY = tripUiState.imageScale
                                    translationX = tripUiState.imageOffsetX
                                    translationY = tripUiState.imageOffsetY
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
                                            colorScheme.scrim.copy(alpha = 0f),
                                            colorScheme.scrim.copy(alpha = 0.7f)
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
                                color = colorScheme.onPrimaryContainer
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
                                    color = colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            if (summaryItems.isNotEmpty()) {
                ElevatedCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Column {
                        summaryItems.forEachIndexed { index, (label, value) ->
                            SummaryListItem(label = label, value = value)
                            if (index < summaryItems.size - 1) {
                                HorizontalDivider()
                            }
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
                onClick = onSaveTrip,
                enabled = tripUiState.tripName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(saveButtonText)
            }
            TextButton(onClick = onDiscard) {
                Text("Discard")
            }
        }
    }
}

@Composable
private fun SummaryListItem(label: String, value: String) {
    ListItem(
        headlineContent = {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        },
        trailingContent = {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Preview(showBackground = true, name = "Full Summary")
@Composable
private fun AddTripSummaryScreenPreview_Full() {
    TripTrackTheme {
        AddTripSummaryContent(
            tripUiState = TripUiState(
                tripName = "Tokyo Adventure",
                currency = "JPY",
                startDate = Instant.now().toEpochMilli(),
                endDate = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli(),
                totalBudget = 200000.0,
                dailyBudget = 25000.0,
                imageUri = "https://images.unsplash.com/photo-1542051841857-5f90071e7989?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&id=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
            ),
            onSaveTrip = {},
            onDiscard = {},
            saveButtonText = "Save Trip"
        )
    }
}

@Preview(showBackground = true, name = "Partial Summary")
@Composable
private fun AddTripSummaryScreenPreview_Partial() {
    TripTrackTheme {
        AddTripSummaryContent(
            tripUiState = TripUiState(
                tripName = "Weekend Getaway",
                currency = "USD",
                startDate = null,
                endDate = null,
                totalBudget = 500.0,
                dailyBudget = null
            ),
            onSaveTrip = {},
            onDiscard = {},
            saveButtonText = "Save Trip"
        )
    }
}
