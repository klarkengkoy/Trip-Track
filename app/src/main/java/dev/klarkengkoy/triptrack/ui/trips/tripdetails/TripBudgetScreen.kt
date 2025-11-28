package dev.klarkengkoy.triptrack.ui.trips.tripdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.klarkengkoy.triptrack.ui.components.AmountVisualTransformation
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.trips.TripsViewModel
import java.util.Currency

@Composable
fun TripBudgetScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onNavigateNext: () -> Unit = {},
    viewModel: TripsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddTripBudgetContent(
        modifier = modifier,
        contentPadding = contentPadding,
        totalBudget = uiState.tripUiState.totalBudget,
        dailyBudget = uiState.tripUiState.dailyBudget,
        currencyCode = uiState.tripUiState.currency,
        onTotalBudgetChanged = {
            val regex = Regex("""^\d*\.?\d{0,2}$""")
            if (it.matches(regex) && it.substringBefore('.').length <= 15) {
                viewModel.onTotalBudgetChanged(it)
            }
        },
        onDailyBudgetChanged = {
            val regex = Regex("""^\d*\.?\d{0,2}$""")
            if (it.matches(regex) && it.substringBefore('.').length <= 15) {
                viewModel.onDailyBudgetChanged(it)
            }
        },
        onNextClicked = onNavigateNext,
        onSkipClicked = {
            // Clear any budget info before navigating
            viewModel.onTotalBudgetChanged("")
            viewModel.onDailyBudgetChanged("")
            onNavigateNext()
        }
    )
}

@Composable
private fun AddTripBudgetContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    totalBudget: String,
    dailyBudget: String,
    currencyCode: String,
    onTotalBudgetChanged: (String) -> Unit,
    onDailyBudgetChanged: (String) -> Unit,
    onNextClicked: () -> Unit,
    onSkipClicked: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val currencySymbol = remember(currencyCode) {
        try {
            if (currencyCode.isNotBlank()) Currency.getInstance(currencyCode).symbol else ""
        } catch (_: Exception) {
            "" // Fallback for invalid codes
        }
    }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set your budget",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                color = colorScheme.onSurface
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.secondaryContainer)
            ) {
                BudgetInputListItem(
                    label = "Total Budget",
                    value = totalBudget,
                    currencySymbol = currencySymbol,
                    onValueChanged = onTotalBudgetChanged
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("and / or", modifier = Modifier.padding(horizontal = 8.dp), color = colorScheme.onSurfaceVariant)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = colorScheme.secondaryContainer)
            ) {
                BudgetInputListItem(
                    label = "Daily Budget",
                    value = dailyBudget,
                    currencySymbol = currencySymbol,
                    onValueChanged = onDailyBudgetChanged
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = contentPadding.calculateBottomPadding() + 16.dp
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val budgetIsSet = totalBudget.isNotEmpty() || dailyBudget.isNotEmpty()
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
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    ListItem(
        colors = ListItemDefaults.colors(
            headlineColor = colorScheme.onSecondaryContainer,
            containerColor = Color.Transparent
        ),
        headlineContent = {
            Text(label)
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currencySymbol.isNotBlank()) {
                    Text(currencySymbol, style = typography.bodyLarge, color = colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(4.dp))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChanged,
                    textStyle = typography.bodyLarge.copy(
                        color = colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.End
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    visualTransformation = AmountVisualTransformation(currencySymbol),
                    singleLine = true,
                    cursorBrush = SolidColor(colorScheme.onSecondaryContainer)
                )
            }
        }
    )
}

@Preview(showBackground = true, name = "With Budget")
@Composable
private fun AddTripBudgetScreenPreview_WithBudget() {
    TripTrackTheme {
        AddTripBudgetContent(
            totalBudget = "1000.00",
            dailyBudget = "100.00",
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
            totalBudget = "",
            dailyBudget = "",
            currencyCode = "USD",
            onTotalBudgetChanged = {},
            onDailyBudgetChanged = {},
            onNextClicked = {},
            onSkipClicked = {}
        )
    }
}
