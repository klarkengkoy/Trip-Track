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
import dev.klarkengkoy.triptrack.ui.trips.AddTripUiState
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            addTripUiState = uiState.addTripUiState,
            onCurrencyClick = onCurrencyClick,
            onNextClicked = onNavigateNext,
            onCustomCurrencyChanged = { viewModel.onCustomCurrencyChanged(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTripCurrencyContent(
    modifier: Modifier = Modifier,
    addTripUiState: AddTripUiState,
    onCurrencyClick: () -> Unit,
    onNextClicked: () -> Unit,
    onCustomCurrencyChanged: (String) -> Unit
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
                text = "Select your currency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                color = colorScheme.onSurface
            )

            ElevatedCard(
                onClick = onCurrencyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = colorScheme.surface),
                    headlineContent = { Text("Currency", style = MaterialTheme.typography.bodyLarge) },
                    trailingContent = {
                        val currencyText = if (addTripUiState.isCurrencyCustom) "Select" else addTripUiState.currency
                        Text(currencyText.ifEmpty { "Select" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    }
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("or", modifier = Modifier.padding(horizontal = 8.dp), color = colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Text(
                text = "Add a custom currency instead",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                color = colorScheme.onSurface
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                val customCurrency = if (addTripUiState.isCurrencyCustom) addTripUiState.currency else ""
                BasicTextField(
                    value = customCurrency,
                    onValueChange = onCustomCurrencyChanged,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    cursorBrush = SolidColor(colorScheme.onSurface),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (customCurrency.isEmpty()) {
                                Text(
                                    text = "e.g., BTC or Credits",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = colorScheme.onSurfaceVariant,
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
            enabled = addTripUiState.currency.isNotEmpty(),
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
            addTripUiState = AddTripUiState(currency = "USD"),
            onCurrencyClick = {},
            onNextClicked = {},
            onCustomCurrencyChanged = {}
        )
    }
}
