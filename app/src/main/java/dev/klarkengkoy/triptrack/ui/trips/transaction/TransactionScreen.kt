package dev.klarkengkoy.triptrack.ui.trips.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.klarkengkoy.triptrack.model.PaymentMethod
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.theme.customColors
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = hiltViewModel(),
    onSave: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isDatePickerVisible) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { viewModel.showDatePicker(false) },
            confirmButton = {
                TextButton(onClick = { viewModel.onDateChange(datePickerState.selectedDateMillis) }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDatePicker(false) }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    TransactionContent(
        modifier = modifier,
        uiState = uiState,
        onAmountChange = viewModel::onAmountChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDateClick = { viewModel.showDatePicker(true) },
        onPaymentMethodChange = viewModel::onPaymentMethodChange,
        onLocationChange = viewModel::onLocationChange,
        onSave = {
            viewModel.saveTransaction()
            onSave()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionContent(
    modifier: Modifier = Modifier,
    uiState: TransactionUiState,
    onAmountChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onDateClick: () -> Unit = {},
    onPaymentMethodChange: (PaymentMethod) -> Unit = {},
    onLocationChange: (String) -> Unit = {},
    onSave: () -> Unit = {}
) {
    var isPaymentMethodMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val categoryColor = when (uiState.categoryTitle) {
                "Accommodation" -> MaterialTheme.customColors.accommodation
                "Activities" -> MaterialTheme.customColors.activities
                "Drinks" -> MaterialTheme.customColors.drinks
                "Entertainment" -> MaterialTheme.customColors.entertainment
                "Fees & Charges" -> MaterialTheme.customColors.feesAndCharges
                "Flights" -> MaterialTheme.customColors.flights
                "General" -> MaterialTheme.customColors.general
                "Gifts & Souvenirs" -> MaterialTheme.customColors.giftsAndSouvenirs
                "Groceries" -> MaterialTheme.customColors.groceries
                "Insurance" -> MaterialTheme.customColors.insurance
                "Laundry" -> MaterialTheme.customColors.laundry
                "Restaurants" -> MaterialTheme.customColors.restaurants
                "Shopping" -> MaterialTheme.customColors.shopping
                "Tours & Entry" -> MaterialTheme.customColors.toursAndEntry
                "Transportation" -> MaterialTheme.customColors.transportation
                "Salary" -> MaterialTheme.customColors.salary
                "Gifts" -> MaterialTheme.customColors.gift
                "Other Income" -> MaterialTheme.customColors.other
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = onAmountChange,
                label = { Text(uiState.categoryTitle) },
                suffix = { Text(uiState.currencySymbol) },
                leadingIcon = {
                    if (uiState.categoryIcon != null) {
                        Icon(
                            imageVector = uiState.categoryIcon,
                            contentDescription = null,
                            tint = categoryColor
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text("Description (Optional)") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                onValueChange = {},
                label = { Text("Date") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDateClick),
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            ExposedDropdownMenuBox(
                expanded = isPaymentMethodMenuExpanded,
                onExpandedChange = { isPaymentMethodMenuExpanded = !isPaymentMethodMenuExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val paymentMethodIcon = when (uiState.paymentMethod?.name) {
                    "Cash" -> Icons.Default.Money
                    "Credit Card" -> Icons.Default.CreditCard
                    "Debit Card" -> Icons.Default.Wallet
                    "Bank Transfer" -> Icons.Default.AccountBalance
                    else -> Icons.Default.Wallet
                }

                OutlinedTextField(
                    value = uiState.paymentMethod?.name ?: "",
                    onValueChange = {},
                    label = { Text("Payment Method") },
                    leadingIcon = { Icon(paymentMethodIcon, contentDescription = null) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPaymentMethodMenuExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                ExposedDropdownMenu(
                    expanded = isPaymentMethodMenuExpanded,
                    onDismissRequest = { isPaymentMethodMenuExpanded = false })
                {
                    uiState.availablePaymentMethods.forEach { paymentMethod ->
                        val icon = when (paymentMethod.name) {
                            "Cash" -> Icons.Default.Money
                            "Credit Card" -> Icons.Default.CreditCard
                            "Debit Card" -> Icons.Default.Wallet
                            "Bank Transfer" -> Icons.Default.AccountBalance
                            else -> Icons.Default.Wallet
                        }
                        DropdownMenuItem(
                            text = { Text(paymentMethod.name) },
                            leadingIcon = { Icon(icon, contentDescription = null) },
                            onClick = {
                                onPaymentMethodChange(paymentMethod)
                                isPaymentMethodMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.location,
                onValueChange = onLocationChange,
                label = { Text("Location (Optional)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Button(
            onClick = onSave,
            enabled = uiState.amount.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text("Save Transaction")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionContentPreview() {
    TripTrackTheme {
        TransactionContent(
            uiState = TransactionUiState(
                amount = "",
                description = "",
                categoryTitle = "General",
                currencySymbol = "$"
            )
        )
    }
}
