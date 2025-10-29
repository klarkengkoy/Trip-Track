package dev.klarkengkoy.triptrack.ui.trips.addtrip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyListScreen(
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
        CurrencyListContent(
            modifier = Modifier.padding(paddingValues),
            onCurrencySelected = onCurrencySelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyListContent(
    modifier: Modifier = Modifier,
    onCurrencySelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allCurrencies = remember { Currency.getAvailableCurrencies().sortedBy { it.currencyCode } }

    val filteredCurrencies = if (searchQuery.isEmpty()) {
        allCurrencies
    } else {
        allCurrencies.filter {
            it.currencyCode.contains(searchQuery, ignoreCase = true) ||
                    it.displayName.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        val onActiveChange: (Boolean) -> Unit = {}
        val colors1 = SearchBarDefaults.colors()
        /* Content is not used in docked mode */DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {},
                expanded = false,
                onExpandedChange = onActiveChange,
                placeholder = { Text("Search currency") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = colors1.inputFieldColors,
            )
        },
        expanded = false, // DockedSearchBar is never active
        onExpandedChange = onActiveChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = SearchBarDefaults.dockedShape,
        colors = colors1,
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        /* Content is not used in docked mode */
        content = { /* Content is not used in docked mode */ },
    )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredCurrencies) { currency ->
                ListItem(
                    headlineContent = { Text(currency.displayName) },
                    trailingContent = { Text(currency.currencyCode) },
                    modifier = Modifier.clickable { onCurrencySelected(currency.currencyCode) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyListScreenPreview() {
    TripTrackTheme {
        CurrencyListContent(onCurrencySelected = {})
    }
}
