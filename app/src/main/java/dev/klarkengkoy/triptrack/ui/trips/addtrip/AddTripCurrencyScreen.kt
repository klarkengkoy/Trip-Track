package dev.klarkengkoy.triptrack.ui.trips.addtrip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripCurrencyScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onNavigateNext: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
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
        AddTripCurrencyContent(
            modifier = Modifier.padding(paddingValues),
            selectedCurrency = uiState.addTripUiState.currency,
            onCurrencyClick = onCurrencyClick,
            onNextClicked = onNavigateNext,
            onCurrencyChanged = { viewModel.onCurrencyChanged(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTripCurrencyContent(
    modifier: Modifier = Modifier,
    selectedCurrency: String,
    onCurrencyClick: () -> Unit,
    onNextClicked: () -> Unit,
    onCurrencyChanged: (String) -> Unit
) {
    var customCurrencyInput by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select your currency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            ElevatedCard(
                onClick = onCurrencyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Currency", style = MaterialTheme.typography.bodyLarge) },
                    trailingContent = { Text(selectedCurrency.ifEmpty { "Select" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold) }
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

            // This text now has the correct, consistent style
            Text(
                text = "Add a custom currency instead",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = customCurrencyInput,
                    onValueChange = {
                        customCurrencyInput = it
                        onCurrencyChanged(it)
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (customCurrencyInput.isEmpty()) {
                                Text(
                                    text = "e.g., BTC or Credits",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            enabled = selectedCurrency.isNotEmpty(),
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
private fun AddTripCurrencyScreenPreview() {
    TripTrackTheme {
        AddTripCurrencyContent(
            selectedCurrency = "USD",
            onCurrencyClick = {},
            onNextClicked = {},
            onCurrencyChanged = {}
        )
    }
}
