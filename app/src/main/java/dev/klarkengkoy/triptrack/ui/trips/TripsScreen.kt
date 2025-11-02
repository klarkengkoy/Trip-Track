package dev.klarkengkoy.triptrack.ui.trips

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.model.Category
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.TransactionType
import dev.klarkengkoy.triptrack.model.Trip
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale


/**
 * The "smart" composable that retains the ViewModel and state.
 */
@Composable
fun TripsScreen(
    modifier: Modifier = Modifier,
    viewModel: TripsViewModel = hiltViewModel(),
    onAddTrip: () -> Unit,
    onAddTransaction: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    TripsScreenContent(
        modifier = modifier,
        uiState = uiState,
        onSelectTrip = { trip -> viewModel.selectTrip(trip) },
        onUnselectTrip = { viewModel.unselectTrip() },
        onAddTrip = onAddTrip,
        onAddTransaction = {
            uiState.selectedTrip?.let { onAddTransaction(it.id) }
        },
    )
}

/**
 * The "dumb" composable that is stateless and easy to preview.
 */
@Composable
private fun TripsScreenContent(
    modifier: Modifier = Modifier,
    uiState: TripsUiState,
    onSelectTrip: (Trip) -> Unit,
    onUnselectTrip: () -> Unit,
    onAddTrip: () -> Unit,
    onAddTransaction: () -> Unit,
) {
    val selectedTrip = uiState.selectedTrip

    BackHandler(enabled = selectedTrip != null) {
        onUnselectTrip()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TripsTopAppBar(
                selectedTrip = selectedTrip,
            )

            if (selectedTrip != null) {
                TripDetailsContent(
                    trip = selectedTrip,
                    transactions = uiState.selectedTripTransactions,
                )
            } else {
                TripsListContent(
                    trips = uiState.trips,
                    onTripSelected = onSelectTrip
                )
            }
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            onClick = {
                if (selectedTrip != null) {
                    onAddTransaction()
                } else {
                    onAddTrip()
                }
            },
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text(if (selectedTrip != null) "New Transaction" else "New Trip") }
        )
    }
}

@Composable
private fun TripsTopAppBar(
    selectedTrip: Trip?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = selectedTrip?.name ?: stringResource(id = R.string.title_trips),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun TripDetailsContent(
    trip: Trip,
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val totalAmount = transactions.sumOf { it.amount }
    val averageAmount = if (transactions.isNotEmpty()) totalAmount / transactions.size else 0.0
    val currencySymbol = Currency.getInstance(trip.currency).symbol


    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                label = "Total",
                value = totalAmount,
                currencySymbol = currencySymbol,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Average",
                value = averageAmount,
                currencySymbol = currencySymbol,
                modifier = Modifier.weight(1f)
            )
        }

        val groupedByDate = transactions.sortedByDescending { it.date }.groupBy { it.date }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedByDate.forEach { (date, transactionsOnDate) ->
                item {
                    DateSeparator(date = date)
                }
                items(transactionsOnDate) { transaction ->
                    TransactionListItem(transaction = transaction, currencySymbol = currencySymbol)
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$currencySymbol${String.format(Locale.US, "%,.2f", value)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
private fun DateSeparator(date: LocalDate) {
    Text(
        text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}


@Composable
private fun TransactionListItem(transaction: Transaction, currencySymbol: String) {
    val notes = transaction.notes
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            headlineContent = {
                Text(text = if (notes.isNullOrBlank()) transaction.category.name else notes)
            },
            supportingContent = {
                if (!notes.isNullOrBlank()) {
                    Text(text = transaction.category.name)
                }
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart, // Placeholder
                    contentDescription = transaction.category.name,
                )
            },
            trailingContent = {
                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}


@Composable
private fun TripsListContent(
    trips: List<Trip>,
    onTripSelected: (Trip) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(trips) { trip ->
            TripListItem(
                trip = trip,
                modifier = Modifier.clickable { onTripSelected(trip) }
            )
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

    if (trip.imageUri != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = trip.imageUri,
                    contentDescription = "Cover photo for ${trip.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Scrim for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 300f
                            )
                        )
                )
                // Content
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = trip.name, style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateRange,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    } else {
        ElevatedCard(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = trip.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Full Screen - Trip Selected")
@Composable
private fun TripsScreenPreview_TripSelected() {
    val sampleTrip = Trip(
        name = "Tokyo Adventure",
        currency = "JPY",
        startDate = LocalDate.of(2024, 5, 1),
        endDate = LocalDate.of(2024, 5, 10),
        totalBudget = 200000.0
    )
    val sampleTransactions = listOf(
        Transaction(
            tripId = sampleTrip.id,
            notes = null,
            amount = 1500.0,
            date = LocalDate.now(),
            category = Category(name = "Food", iconRes = 0),
            paymentMethod = dev.klarkengkoy.triptrack.model.PaymentMethod(name = "Cash", iconRes = 0),
            type = TransactionType.EXPENSE
        ),
        Transaction(
            tripId = sampleTrip.id,
            notes = "Subway ticket",
            amount = 300.0,
            date = LocalDate.now(),
            category = Category(name = "Transport", iconRes = 0),
            paymentMethod = dev.klarkengkoy.triptrack.model.PaymentMethod(name = "Cash", iconRes = 0),
            type = TransactionType.EXPENSE
        ),
        Transaction(
            tripId = sampleTrip.id,
            notes = "Museum entrance fee",
            amount = 1000.0,
            date = LocalDate.now().minusDays(1),
            category = Category(name = "Entertainment", iconRes = 0),
            paymentMethod = dev.klarkengkoy.triptrack.model.PaymentMethod(name = "Cash", iconRes = 0),
            type = TransactionType.EXPENSE
        )
    )

    TripTrackTheme {
        TripsScreenContent(
            uiState = TripsUiState(
                selectedTrip = sampleTrip,
                trips = listOf(sampleTrip),
                transactionsByTrip = mapOf(sampleTrip.id to sampleTransactions)
            ),
            onSelectTrip = { },
            onUnselectTrip = { },
            onAddTrip = { },
            onAddTransaction = { },
        )
    }
}

@Preview(showBackground = true, name = "Full Screen - No Trip Selected")
@Composable
private fun TripsScreenPreview_NoTripSelected() {
    val sampleTrips = listOf(
        Trip(
            name = "Tokyo Adventure",
            currency = "JPY",
            startDate = LocalDate.of(2024, 5, 1),
            endDate = LocalDate.of(2024, 5, 10),
            totalBudget = 200000.0,
            imageUri = "placeholder" // Example with image
        ),
        Trip(
            name = "Paris Getaway",
            currency = "EUR",
            startDate = LocalDate.of(2024, 8, 20),
            endDate = LocalDate.of(2024, 8, 27),
            totalBudget = 3000.0
        )
    )

    TripTrackTheme {
        TripsScreenContent(
            uiState = TripsUiState(
                selectedTrip = null,
                trips = sampleTrips,
                transactionsByTrip = emptyMap()
            ),
            onSelectTrip = { },
            onUnselectTrip = { },
            onAddTrip = { },
            onAddTransaction = { },
        )
    }
}
