package dev.klarkengkoy.triptrack.ui.trips.addtrip

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "AddTripDates"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripDatesScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onNavigateNext: () -> Unit = {},
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
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

    // Reactively update the ViewModel and close the sheet when a date range is selected
    LaunchedEffect(dateRangePickerState.selectedEndDateMillis) {
        dateRangePickerState.selectedEndDateMillis?.let {
            Log.d(TAG, "Picker selection changed: startDate=${dateRangePickerState.selectedStartDateMillis}, endDate=${it}")
            viewModel.onDatesChanged(
                dateRangePickerState.selectedStartDateMillis,
                it
            )
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet = false
                }
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        AddTripDatesContent(
            modifier = Modifier.padding(paddingValues),
            startDateMillis = uiState.addTripUiState.startDate,
            endDateMillis = uiState.addTripUiState.endDate,
            onAddDatesClicked = { showBottomSheet = true },
            onNextClicked = onNavigateNext,
            onSkipClicked = onNavigateNext // Both skip and next go to the same place
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            DateRangePicker(state = dateRangePickerState, title = null)
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
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (startDateMillis != null && endDateMillis != null) {
                Text(
                    text = "Your selected dates:",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Surface(
                    tonalElevation = 1.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            headlineContent = { Text("Start", style = MaterialTheme.typography.titleLarge) },
                            trailingContent = { Text(formatDate(startDateMillis), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.clickable { onAddDatesClicked() }
                        )
                        ListItem(
                            headlineContent = { Text("End", style = MaterialTheme.typography.titleLarge) },
                            trailingContent = { Text(formatDate(endDateMillis), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.clickable { onAddDatesClicked() }
                        )
                    }
                }

            } else {
                Text(
                    text = "Include travel dates?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = onAddDatesClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Yes, add dates")
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
                TextButton(onClick = onAddDatesClicked) {
                    Text("Change dates")
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


@Preview(showBackground = true)
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

@Preview(showBackground = true)
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
