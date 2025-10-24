package dev.klarkengkoy.triptrack.ui.trips

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.Trip
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    viewModel: TripsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTrip = uiState.selectedTrip

    Column(modifier = modifier.fillMaxSize()) {
        TripsTopAppBar(
            selectedTrip = selectedTrip,
            onUnselectTrip = { viewModel.unselectTrip() }
        )
        if (selectedTrip != null) {
            TripDetailsContent(
                transactions = uiState.selectedTripTransactions,
            )
        } else {
            TripsListContent(
                trips = uiState.trips,
                onTripSelected = { trip -> viewModel.selectTrip(trip) }
            )
        }
    }
}

@Composable
private fun TripsTopAppBar(
    selectedTrip: Trip?,
    onUnselectTrip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = selectedTrip?.name ?: stringResource(id = R.string.title_trips),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
        if (selectedTrip != null) {
            IconButton(onClick = onUnselectTrip) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back to Trips",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            Spacer(modifier = Modifier.height(0.dp)) // Maintain space
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDetailsContent(transactions: List<Transaction>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
    ) {
        items(transactions) { transaction ->
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart, // Using a standard Material icon as a placeholder
                        contentDescription = transaction.category.name
                    )
                },
                headlineContent = {
                    Text(text = transaction.category.name)
                },
                supportingContent = {
                    if (transaction.notes.isNotBlank()) {
                        Text(text = transaction.notes)
                    }
                },
                trailingContent = {
                    Text(text = "$${String.format(Locale.US, "%.2f", transaction.amount)}")
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun TripsListContent(
    trips: List<Trip>,
    onTripSelected: (Trip) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(trips) { trip ->
            TripListItem(
                trip = trip,
                modifier = Modifier.clickable { onTripSelected(trip) }
            )
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripListItem(trip: Trip, modifier: Modifier = Modifier) {
    val dateRange = if (trip.startDate != null && trip.endDate != null) {
        val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        "${trip.startDate.format(formatter)} - ${trip.endDate.format(formatter)}"
    } else {
        "No dates set"
    }

    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = trip.name, style = MaterialTheme.typography.titleMedium) },
        supportingContent = {
            Text(
                text = dateRange,
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}
