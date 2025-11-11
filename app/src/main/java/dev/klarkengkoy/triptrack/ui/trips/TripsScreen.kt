package dev.klarkengkoy.triptrack.ui.trips

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.model.Category
import dev.klarkengkoy.triptrack.model.PaymentMethod
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.TransactionType
import dev.klarkengkoy.triptrack.model.Trip
import dev.klarkengkoy.triptrack.ui.MainViewModel
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import kotlinx.coroutines.launch
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
    mainViewModel: MainViewModel,
    onAddTrip: () -> Unit,
    onAddTransaction: (String) -> Unit,
    onEditTrip: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TripsScreenContent(
        modifier = modifier,
        uiState = uiState,
        mainViewModel = mainViewModel,
        onSelectTrip = { trip -> viewModel.selectTrip(trip) },
        onUnselectTrip = { viewModel.unselectTrip() },
        onAddTrip = onAddTrip,
        onAddTransaction = {
            uiState.selectedTrip?.let { onAddTransaction(it.id) }
        },
        onEditTrip = {
            uiState.selectedTrip?.let { onEditTrip(it.id) }
        },
        onEnterSelectionMode = { tripId -> viewModel.enterSelectionMode(tripId) },
        onExitSelectionMode = { viewModel.exitSelectionMode() },
        onToggleTripSelection = { tripId -> viewModel.toggleTripSelection(tripId) },
        onSelectAllTrips = { viewModel.selectAllTrips() },
        onClearSelectedTrips = { viewModel.clearSelectedTrips() },
        onDeleteSelectedTrips = { viewModel.deleteSelectedTrips() }
    )
}

/**
 * The "dumb" composable that is stateless and easy to preview.
 */
@Composable
private fun TripsScreenContent(
    modifier: Modifier = Modifier,
    uiState: TripsUiState,
    mainViewModel: MainViewModel,
    onSelectTrip: (Trip) -> Unit,
    onUnselectTrip: () -> Unit,
    onAddTrip: () -> Unit,
    onAddTransaction: () -> Unit,
    onEditTrip: () -> Unit,
    onEnterSelectionMode: (String) -> Unit,
    onExitSelectionMode: () -> Unit,
    onToggleTripSelection: (String) -> Unit,
    onSelectAllTrips: () -> Unit,
    onClearSelectedTrips: () -> Unit,
    onDeleteSelectedTrips: () -> Unit
) {
    val selectedTrip = uiState.selectedTrip
    val selectionMode = uiState.selectionMode

    val onNavigateUp = {
        if (selectionMode) {
            onExitSelectionMode()
        } else {
            onUnselectTrip()
        }
    }

    // Configure the TopAppBar based on the current screen state
    LaunchedEffect(selectedTrip, selectionMode, uiState.selectedTrips) {
        mainViewModel.setTopAppBarState(
            title = {
                Text(
                    text = when {
                        selectionMode -> "Select Trips"
                        selectedTrip != null -> selectedTrip.name
                        else -> stringResource(id = R.string.title_trips)
                    }
                )
            },
            navigationIcon = {
                if (selectedTrip != null || selectionMode) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_navigate_up)
                        )
                    }
                }
            },
            actions = {
                if (selectionMode) {
                    IconToggleButton(checked = uiState.trips.isNotEmpty() && uiState.selectedTrips.size == uiState.trips.size, onCheckedChange = {
                        if (it) onSelectAllTrips() else onClearSelectedTrips()
                    }) {
                        if (uiState.trips.isNotEmpty() && uiState.selectedTrips.size == uiState.trips.size) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Deselect All")
                        } else {
                            Icon(Icons.Filled.RadioButtonUnchecked, contentDescription = "Select All")
                        }
                    }
                } else if (selectedTrip != null) {
                    IconButton(onClick = onEditTrip) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Trip")
                    }
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedTrip != null) {
                TripDetailsContent(
                    trip = selectedTrip,
                    transactions = uiState.selectedTripTransactions,
                )
            } else {
                TripsListContent(
                    trips = uiState.trips,
                    onTripSelected = onSelectTrip,
                    selectionMode = selectionMode,
                    selectedTrips = uiState.selectedTrips,
                    onEnterSelectionMode = onEnterSelectionMode,
                    onToggleTripSelection = onToggleTripSelection
                )
            }
        }

        if (selectionMode) {
            Popup(alignment = Alignment.BottomCenter) {
                TripsBottomBar(
                    selectedCount = uiState.selectedTrips.size,
                    onCancel = onExitSelectionMode,
                    onDelete = onDeleteSelectedTrips
                )
            }
        } else {
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
}

@Composable
private fun TripsBottomBar(
    selectedCount: Int,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$selectedCount selected", style = MaterialTheme.typography.titleMedium)
            Row {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
                IconButton(onClick = onCancel) {
                    Icon(Icons.Filled.Cancel, contentDescription = "Cancel")
                }
            }
        }
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
    val currencySymbol = remember(trip.currency, trip.isCurrencyCustom) {
        if (trip.isCurrencyCustom) {
            trip.currency
        } else {
            try {
                Currency.getInstance(trip.currency).symbol
            } catch (_: Exception) {
                trip.currency // Fallback for safety
            }
        }
    }


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
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
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
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$currencySymbol ${String.format(Locale.US, "%,.2f", value)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                Text(
                    text = if (notes.isNullOrBlank()) transaction.category.name else notes,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            supportingContent = {
                if (!notes.isNullOrBlank()) {
                    Text(
                        text = transaction.category.name,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart, // Placeholder
                    contentDescription = transaction.category.name,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Text(
                    text = "$currencySymbol ${String.format(Locale.US, "%,.2f", transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TripsListContent(
    trips: List<Trip>,
    onTripSelected: (Trip) -> Unit,
    selectionMode: Boolean,
    selectedTrips: Set<String>,
    onEnterSelectionMode: (String) -> Unit,
    onToggleTripSelection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomPadding = 16.dp + if (selectionMode) 80.dp else 72.dp
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = bottomPadding)
    ) {
        items(items = trips, key = { it.id }) { trip ->
            val isSelected = selectedTrips.contains(trip.id)
            TripListItem(
                trip = trip,
                isSelected = isSelected,
                selectionMode = selectionMode,
                onClick = {
                    if (selectionMode) {
                        onToggleTripSelection(trip.id)
                    } else {
                        onTripSelected(trip)
                    }
                },
                onLongClick = { onEnterSelectionMode(trip.id) }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TripListItem(
    trip: Trip,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val wasSelected = remember { mutableStateOf(isSelected) }

    LaunchedEffect(isSelected) {
        if (isSelected && !wasSelected.value) {
            launch {
                scale.animateTo(
                    targetValue = 1.05f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f)
                )
            }
        }
        wasSelected.value = isSelected
    }

    val cardModifier = Modifier
        .fillMaxWidth()
        .scale(scale.value)
        .combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
            indication = null, // Disable ripple to show our own animation
            interactionSource = remember { MutableInteractionSource() }
        )

    Card(
        modifier = cardModifier
            .height(200.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Layer
            if (trip.imageUri != null) {
                AsyncImage(
                    model = trip.imageUri,
                    contentDescription = "Cover photo for ${trip.name}",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = trip.imageScale
                            scaleY = trip.imageScale
                            translationX = trip.imageOffsetX
                            translationY = trip.imageOffsetY
                        },
                    contentScale = ContentScale.Fit
                )
            }
            // Scrim for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0f),
                                MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f)
                            ),
                            startY = 300f
                        )
                    )
            )

            // Selection Overlay
            if (selectionMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = if (isSelected) 0.3f else 0f))
                )
            }

            // Content Layer
            val contentColor = MaterialTheme.colorScheme.onSurface

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = if (trip.imageUri != null) Arrangement.Bottom else Arrangement.Center,
                horizontalAlignment = if (trip.imageUri != null) Alignment.Start else Alignment.CenterHorizontally
            ) {
                Text(
                    text = trip.name, style = MaterialTheme.typography.titleLarge,
                    color = contentColor
                )
                if (trip.startDate != null && trip.endDate != null) {
                    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                    val dateRange = "${trip.startDate.format(formatter)} - ${trip.endDate.format(formatter)}"

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateRange,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor
                    )
                }
            }

            // Selection Checkbox Layer
            if (selectionMode) {
                IconToggleButton(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                ) {
                    if (isSelected) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                    } else {
                        Icon(Icons.Filled.RadioButtonUnchecked, contentDescription = "Not Selected", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
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
            paymentMethod = PaymentMethod(name = "Cash", iconRes = 0),
            type = TransactionType.EXPENSE
        ),
        Transaction(
            tripId = sampleTrip.id,
            notes = "Subway ticket",
            amount = 300.0,
            date = LocalDate.now(),
            category = Category(name = "Transport", iconRes = 0),
            paymentMethod = PaymentMethod(name = "Cash", iconRes = 0),
            type = TransactionType.EXPENSE
        ),
        Transaction(
            tripId = sampleTrip.id,
            notes = "Museum entrance fee",
            amount = 1000.0,
            date = LocalDate.now().minusDays(1),
            category = Category(name = "Entertainment", iconRes = 0),
            paymentMethod = PaymentMethod(name = "Cash", iconRes = 0),
            type = TransactionType.EXPENSE
        )
    )

    TripTrackTheme {
        val uiState = TripsUiState(
            selectedTrip = sampleTrip,
            trips = listOf(sampleTrip),
            transactionsByTrip = mapOf(sampleTrip.id to sampleTransactions)
        )
        TripsScreenContent(
            uiState = uiState,
            mainViewModel = hiltViewModel(),
            onSelectTrip = { },
            onUnselectTrip = { },
            onAddTrip = { },
            onAddTransaction = { },
            onEditTrip = {},
            onEnterSelectionMode = {},
            onExitSelectionMode = {},
            onToggleTripSelection = {},
            onSelectAllTrips = {},
            onClearSelectedTrips = {},
            onDeleteSelectedTrips = {}
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
            mainViewModel = hiltViewModel(),
            onSelectTrip = { },
            onUnselectTrip = { },
            onAddTrip = { },
            onAddTransaction = { },
            onEditTrip = {},
            onEnterSelectionMode = {},
            onExitSelectionMode = {},
            onToggleTripSelection = {},
            onSelectAllTrips = {},
            onClearSelectedTrips = {},
            onDeleteSelectedTrips = {}
        )
    }
}

@Preview(showBackground = true, name = "Full Screen - Selection Mode")
@Composable
private fun TripsScreenPreview_SelectionMode() {
    val sampleTrips = listOf(
        Trip(
            id = "1",
            name = "Tokyo Adventure",
            currency = "JPY",
            startDate = LocalDate.of(2024, 5, 1),
            endDate = LocalDate.of(2024, 5, 10),
            totalBudget = 200000.0,
            imageUri = "placeholder" // Example with image
        ),
        Trip(
            id = "2",
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
                selectionMode = true,
                selectedTrips = setOf("1"),
                trips = sampleTrips
            ),
            mainViewModel = hiltViewModel(),
            onSelectTrip = { },
            onUnselectTrip = { },
            onAddTrip = { },
            onAddTransaction = { },
            onEditTrip = {},
            onEnterSelectionMode = {},
            onExitSelectionMode = {},
            onToggleTripSelection = {},
            onSelectAllTrips = {},
            onClearSelectedTrips = {},
            onDeleteSelectedTrips = {}
        )
    }
}
