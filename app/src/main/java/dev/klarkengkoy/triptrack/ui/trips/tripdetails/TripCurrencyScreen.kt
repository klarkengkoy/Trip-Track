package dev.klarkengkoy.triptrack.ui.trips.tripdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripUiState
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel

@Composable
fun TripCurrencyScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onNavigateNext: () -> Unit = {},
    onCurrencyClick: () -> Unit = {},
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TripCurrencyContent(
        modifier = modifier,
        contentPadding = contentPadding,
        tripUiState = uiState.tripUiState,
        onCurrencyClick = onCurrencyClick,
        onNextClicked = onNavigateNext,
        onCustomCurrencyChanged = { viewModel.onCustomCurrencyChanged(it) }
    )
}

@Composable
private fun TripCurrencyContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    tripUiState: TripUiState,
    onCurrencyClick: () -> Unit,
    onNextClicked: () -> Unit,
    onCustomCurrencyChanged: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = contentPadding.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select currency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.onSurface
            )

            ElevatedCard(
                onClick = onCurrencyClick,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                ListItem(
                    colors = ListItemDefaults.colors(
                        headlineColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        containerColor = Color.Transparent
                    ),
                    headlineContent = { Text("Currency", style = MaterialTheme.typography.bodyLarge) },
                    trailingContent = {
                        // Logic Change: If isCurrencyCustom is TRUE, we show "Select" here (clearing this field essentially).
                        // If isCurrencyCustom is FALSE and currency is populated, we show the currency.
                        val currencyText = if (tripUiState.isCurrencyCustom || tripUiState.currency.isBlank()) {
                            "Select"
                        } else {
                            tripUiState.currency
                        }
                        
                        Text(
                            text = currencyText, 
                            style = MaterialTheme.typography.bodyLarge, 
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    "or",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Text(
                text = "Enter custom currency",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = colorScheme.onSurface
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                // Logic Change: Only show the custom currency value if isCurrencyCustom is TRUE
                val customCurrency = if (tripUiState.isCurrencyCustom) tripUiState.currency else ""
                // TODO(klarkengkoy): Inside ui>tripdetails>tripcurrencyscreen, we will also put some validations like what we did here in Transaction amount.
                BasicTextField(
                    value = customCurrency,
                    onValueChange = onCustomCurrencyChanged,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
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
                            if (customCurrency.isEmpty()) {
                                Text(
                                    text = "e.g., BTC or Credits",
                                    style = MaterialTheme.typography.bodyLarge,
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
            enabled = tripUiState.currency.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp
                )
                .align(Alignment.BottomCenter)
        ) {
            Text("Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripCurrencyScreenPreview() {
    TripTrackTheme {
        TripCurrencyContent(
            tripUiState = TripUiState(currency = "USD"),
            onCurrencyClick = {},
            onNextClicked = {},
            onCustomCurrencyChanged = {}
        )
    }
}
