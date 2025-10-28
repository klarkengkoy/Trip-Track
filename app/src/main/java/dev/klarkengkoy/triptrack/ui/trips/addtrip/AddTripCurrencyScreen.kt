package dev.klarkengkoy.triptrack.ui.trips.addtrip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripCurrencyScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onCurrencySelected: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Select Currency") },
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
            onCurrencySelected = onCurrencySelected
        )
    }
}

@Composable
private fun AddTripCurrencyContent(
    modifier: Modifier = Modifier,
    onCurrencySelected: (String) -> Unit
) {
    // A mock list for now. We can make this more robust later.
    val currencies = listOf("USD", "EUR", "JPY", "GBP", "PHP")

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            ListItem(
                headlineContent = { Text("Add Custom Currency") }
            )
        }
        items(currencies.size) { index ->
            val currency = currencies[index]
            ListItem(
                headlineContent = { Text(currency) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTripCurrencyScreenPreview() {
    TripTrackTheme {
        AddTripCurrencyContent(onCurrencySelected = {})
    }
}
