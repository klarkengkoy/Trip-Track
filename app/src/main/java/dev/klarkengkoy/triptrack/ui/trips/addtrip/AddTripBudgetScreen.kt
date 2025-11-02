package dev.klarkengkoy.triptrack.ui.trips.addtrip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripBudgetScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onNavigateNext: () -> Unit = {},
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
        AddTripBudgetContent(
            modifier = Modifier.padding(paddingValues),
            totalBudget = uiState.addTripUiState.totalBudget,
            dailyBudget = uiState.addTripUiState.dailyBudget,
            currencyCode = uiState.addTripUiState.currency,
            onTotalBudgetChanged = { viewModel.onTotalBudgetChanged(it) },
            onDailyBudgetChanged = { viewModel.onDailyBudgetChanged(it) },
            onNextClicked = onNavigateNext,
            onSkipClicked = {
                // Clear any budget info before navigating
                viewModel.onTotalBudgetChanged("")
                onNavigateNext()
            }
        )
    }
}

@Composable
private fun AddTripBudgetContent(
    modifier: Modifier = Modifier,
    totalBudget: Double?,
    dailyBudget: Double?,
    currencyCode: String,
    onTotalBudgetChanged: (String) -> Unit,
    onDailyBudgetChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    var totalBudgetInput by remember { mutableStateOf("") }
    var dailyBudgetInput by remember { mutableStateOf("") }

    LaunchedEffect(totalBudget) {
        if (totalBudgetInput.toDoubleOrNull() != totalBudget) {
            totalBudgetInput = formatBudget(totalBudget)
        }
    }

    LaunchedEffect(dailyBudget) {
        if (dailyBudgetInput.toDoubleOrNull() != dailyBudget) {
            dailyBudgetInput = formatBudget(dailyBudget)
        }
    }

    val currencySymbol = remember(currencyCode) {
        try {
            if (currencyCode.isNotBlank()) Currency.getInstance(currencyCode).symbol else ""
        } catch (e: Exception) {
            "" // Fallback for invalid codes
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
                text = "Set your budget",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                BudgetInputListItem(
                    label = "Total Budget",
                    value = totalBudgetInput,
                    currencySymbol = currencySymbol,
                    onValueChanged = {
                        totalBudgetInput = it
                        onTotalBudgetChanged(it)
                    }
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("or", modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                BudgetInputListItem(
                    label = "Daily Budget",
                    value = dailyBudgetInput,
                    currencySymbol = currencySymbol,
                    onValueChanged = {
                        dailyBudgetInput = it
                        onDailyBudgetChanged(it)
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val budgetIsSet = totalBudget != null || dailyBudget != null
            if (budgetIsSet) {
                Button(
                    onClick = onNextClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next")
                }
            }
            TextButton(onClick = onSkipClicked) {
                Text("Skip for now")
            }
        }
    }
}

@Composable
private fun BudgetInputListItem(
    label: String,
    value: String,
    currencySymbol: String,
    onValueChanged: (String) -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(label)
        },
        trailingContent = {
            BasicTextField(
                value = value,
                onValueChange = onValueChanged,
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currencySymbol.isNotBlank()) {
                            Text(currencySymbol, style = MaterialTheme.typography.bodyLarge)
                        }
                        innerTextField()
                    }
                }
            )
        }
    )
}

private fun formatBudget(budget: Double?): String {
    if (budget == null) return ""
    // If it's a whole number, display as an integer.
    return if (budget % 1.0 == 0.0) {
        budget.toLong().toString()
    } else {
        // Otherwise, format to 2 decimal places.
        String.format(Locale.US, "%.2f", budget)
    }
}

@Preview(showBackground = true, name = "With Budget")
@Composable
private fun AddTripBudgetScreenPreview_WithBudget() {
    TripTrackTheme {
        AddTripBudgetContent(
            totalBudget = 1000.0,
            dailyBudget = 100.0,
            currencyCode = "USD",
            onTotalBudgetChanged = {},
            onDailyBudgetChanged = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}

@Preview(showBackground = true, name = "No Budget")
@Composable
private fun AddTripBudgetScreenPreview_NoBudget() {
    TripTrackTheme {
        AddTripBudgetContent(
            totalBudget = null,
            dailyBudget = null,
            currencyCode = "USD",
            onTotalBudgetChanged = {},
            onDailyBudgetChanged = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}
