package dev.klarkengkoy.triptrack.ui.trips.addtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.AddTripUiState
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Currency

@Composable
fun AddTripSummaryScreen(
    onNavigateUp: () -> Unit,
    onSaveTrip: () -> Unit,
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    AddTripSummaryContent(
        addTripUiState = uiState.addTripUiState,
        onNavigateUp = onNavigateUp,
        onSaveTrip = onSaveTrip
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTripSummaryContent(
    modifier: Modifier = Modifier,
    addTripUiState: AddTripUiState,
    onNavigateUp: () -> Unit,
    onSaveTrip: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    val summaryItems = remember(addTripUiState) {
        buildList {
            if (addTripUiState.tripName.isNotBlank()) {
                add("Trip Name" to addTripUiState.tripName)
            }
            if (addTripUiState.currency.isNotBlank()) {
                val currencyLabel = try {
                    val currency = Currency.getInstance(addTripUiState.currency)
                    "${currency.displayName} (${currency.symbol})"
                } catch (e: Exception) {
                    addTripUiState.currency // Fallback to code
                }
                add("Currency" to currencyLabel)
            }
            addTripUiState.startDate?.let {
                val formatted = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                add("Start Date" to formatted)
            }
            addTripUiState.endDate?.let {
                val formatted = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(dateFormatter)
                add("End Date" to formatted)
            }
            addTripUiState.totalBudget?.let {
                val format = if (addTripUiState.currency.isNotBlank()) {
                    NumberFormat.getCurrencyInstance().apply {
                        try {
                            this.currency = Currency.getInstance(addTripUiState.currency)
                        } catch (e: Exception) { /* Ignore */ }
                    }
                } else {
                    NumberFormat.getNumberInstance()
                }
                add("Total Budget" to format.format(it))
            }
            addTripUiState.dailyBudget?.let {
                val format = if (addTripUiState.currency.isNotBlank()) {
                    NumberFormat.getCurrencyInstance().apply {
                        try {
                            this.currency = Currency.getInstance(addTripUiState.currency)
                        } catch (e: Exception) { /* Ignore */ }
                    }
                } else {
                    NumberFormat.getNumberInstance()
                }
                add("Daily Budget" to format.format(it))
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (summaryItems.isNotEmpty()) {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
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

            Button(
                onClick = onSaveTrip,
                enabled = addTripUiState.tripName.isNotBlank(), // Button is enabled only if there's a trip name
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text("Save Trip")
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
            containerColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true, name = "Full Summary")
@Composable
private fun AddTripSummaryScreenPreview_Full() {
    TripTrackTheme {
        AddTripSummaryContent(
            addTripUiState = AddTripUiState(
                tripName = "Tokyo Adventure",
                currency = "JPY",
                startDate = Instant.now().toEpochMilli(),
                endDate = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli(),
                totalBudget = 200000.0,
                dailyBudget = 25000.0
            ),
            onNavigateUp = {},
            onSaveTrip = {}
        )
    }
}

@Preview(showBackground = true, name = "Partial Summary")
@Composable
private fun AddTripSummaryScreenPreview_Partial() {
    TripTrackTheme {
        AddTripSummaryContent(
            addTripUiState = AddTripUiState(
                tripName = "Weekend Getaway",
                currency = "USD",
                startDate = null,
                endDate = null,
                totalBudget = 500.0,
                dailyBudget = null
            ),
            onNavigateUp = {},
            onSaveTrip = {}
        )
    }
}