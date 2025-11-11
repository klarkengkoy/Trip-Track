package dev.klarkengkoy.triptrack.ui.trips.tripdetails

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "AddTripDates"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripDatesScreen(
    modifier: Modifier = Modifier,
    onNavigateNext: () -> Unit = {},
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = uiState.addTripUiState.startDate,
        initialSelectedEndDateMillis = uiState.addTripUiState.endDate
    )

    // Sync the picker state with the ViewModel state when the screen first loads
    LaunchedEffect(uiState.addTripUiState.startDate, uiState.addTripUiState.endDate) {
        if (uiState.addTripUiState.startDate != null) {
            dateRangePickerState.setSelection(
                startDateMillis = uiState.addTripUiState.startDate,
                endDateMillis = uiState.addTripUiState.endDate
            )
        }
    }

    AddTripDatesContent(
        modifier = modifier,
        startDateMillis = uiState.addTripUiState.startDate,
        endDateMillis = uiState.addTripUiState.endDate,
        onAddDatesClicked = { setShowDatePicker(true) },
        onNextClicked = onNavigateNext,
        onSkipClicked = {
            viewModel.onDatesChanged(null, null) // Clear the dates
            onNavigateNext()                     // Then navigate
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { setShowDatePicker(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        dateRangePickerState.selectedEndDateMillis?.let {
                            Log.d(
                                TAG,
                                "Picker selection changed: startDate=${dateRangePickerState.selectedStartDateMillis}, endDate=${it}"
                            )
                            viewModel.onDatesChanged(
                                dateRangePickerState.selectedStartDateMillis,
                                it
                            )
                        }
                        setShowDatePicker(false)
                    },
                    enabled = dateRangePickerState.selectedEndDateMillis != null
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        setShowDatePicker(false)
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = null,
                headline = {
                    val startDate = dateRangePickerState.selectedStartDateMillis
                    val endDate = dateRangePickerState.selectedEndDateMillis
                    val startDateText = if (startDate != null) formatDate(startDate) else "Start"
                    val endDateText = if (endDate != null) formatDate(endDate) else "End"

                    Text(
                        text = "$startDateText - $endDateText",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}

@Composable
private fun AddTripDatesContent(
    modifier: Modifier = Modifier,
    startDateMillis: Long?,
    endDateMillis: Long?,
    onAddDatesClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (startDateMillis != null) "Your selected dates:" else "Include travel dates?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                color = colorScheme.onSurface
            )

            ElevatedCard(
                onClick = onAddDatesClicked,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                if (startDateMillis != null && endDateMillis != null) {
                    Column {
                        ListItem(
                            colors = ListItemDefaults.colors(
                                headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = Color.Transparent
                            ),
                            headlineContent = { Text("Start", style = MaterialTheme.typography.bodyLarge) },
                            trailingContent = { Text(formatDate(startDateMillis), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer) }
                        )
                        HorizontalDivider()
                        ListItem(
                            colors = ListItemDefaults.colors(
                                headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                containerColor = Color.Transparent
                            ),
                            headlineContent = { Text("End", style = MaterialTheme.typography.bodyLarge) },
                            trailingContent = { Text(formatDate(endDateMillis), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSecondaryContainer) }
                        )
                    }
                } else {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = {
                            Text(
                                text = "Yes, add dates",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    )
                }
            }
        }

        // Next/Skip button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (startDateMillis != null && endDateMillis != null) {
                Button(
                    onClick = onNextClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next")
                }
                TextButton(onClick = onSkipClicked) {
                    Text("Skip for now")
                }
            } else {
                TextButton(onClick = onSkipClicked) {
                    Text("Skip for now")
                }
            }
        }
    }
}

private fun formatDate(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy").withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}

@Preview(showBackground = true, name = "No Dates Selected")
@Composable
private fun AddTripDatesScreenPreview_NoDates() {
    TripTrackTheme {
        AddTripDatesContent(
            startDateMillis = null,
            endDateMillis = null,
            onAddDatesClicked = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "Dates Selected")
@Composable
private fun AddTripDatesScreenPreview_WithDates() {
    TripTrackTheme {
        AddTripDatesContent(
            startDateMillis = 1672531200000, // Jan 1, 2023
            endDateMillis = 1673318400000,   // Jan 10, 2023
            onAddDatesClicked = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}
