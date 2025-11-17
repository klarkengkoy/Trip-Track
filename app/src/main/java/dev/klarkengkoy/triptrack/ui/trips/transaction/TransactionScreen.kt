package dev.klarkengkoy.triptrack.ui.trips.transaction

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import dev.klarkengkoy.triptrack.R
import dev.klarkengkoy.triptrack.model.PaymentMethod
import dev.klarkengkoy.triptrack.ui.theme.TripTrackTheme
import dev.klarkengkoy.triptrack.ui.theme.customColors
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private class AmountVisualTransformation(
    private val currencySymbol: String
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Format the integer part with thousand separators
        val integerPart = originalText.substringBefore('.')
        val decimalPart = originalText.substringAfter('.', "")

        val formattedInteger = if (integerPart.isNotEmpty()) {
            NumberFormat.getNumberInstance(Locale.US).format(integerPart.toLong())
        } else {
            ""
        }

        val formattedText = if (decimalPart.isNotEmpty() || originalText.endsWith(".")) {
            "$formattedInteger.$decimalPart"
        } else {
            formattedInteger
        }

        val annotatedString = AnnotatedString("$formattedText $currencySymbol")

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val originalIntegerLen = integerPart.length
                val formattedIntegerLen = formattedInteger.length
                val commas = formattedIntegerLen - originalIntegerLen

                return when {
                    offset == 0 -> 0
                    offset <= originalIntegerLen -> {
                        // Count commas before the cursor in the integer part
                        val textBeforeCursor = integerPart.take(offset)
                        val formattedTextBeforeCursor = NumberFormat.getNumberInstance(Locale.US).format(textBeforeCursor.toLong())
                        offset + formattedTextBeforeCursor.count { it == ',' }
                    }
                    else -> offset + commas // Cursor is in the decimal part
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val commas = formattedText.take(offset).count { it == ',' }
                return (offset - commas).coerceIn(0, originalText.length)
            }
        }
        return TransformedText(annotatedString, offsetMapping)
    }
}

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
                    Text(stringResource(R.string.transaction_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDatePicker(false) }) {
                    Text(stringResource(R.string.transaction_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, flag)
                viewModel.onImageChange(it)
            }
        }
    )

    TransactionContent(
        modifier = modifier,
        uiState = uiState,
        onAmountChange = {
            val regex = Regex("^\\d*\\.?\\d{0,2}$")
            if (it.matches(regex)) {
                viewModel.onAmountChange(it)
            }
        },
        onDescriptionChange = viewModel::onDescriptionChange,
        onDateClick = { viewModel.showDatePicker(true) },
        onPaymentMethodChange = viewModel::onPaymentMethodChange,
        onPaymentMethodMenuToggled = viewModel::onPaymentMethodMenuToggled,
        onLocationChange = viewModel::onLocationChange,
        onAddPhotoClick = { imagePickerLauncher.launch("image/*") },
        onSave = {
            // TODO(klarkengkoy): Complete Save Transaction feature
            viewModel.saveTransaction()
            onSave()
        }
    )
}

@Composable
private fun paymentMethodIcon(paymentMethodName: String?): ImageVector {
    return when (paymentMethodName) {
        "Cash" -> Icons.Default.Money
        "Credit Card" -> Icons.Default.CreditCard
        "Debit Card" -> Icons.Default.Wallet
        "Bank Transfer" -> Icons.Default.AccountBalance
        else -> Icons.Default.Wallet
    }
}

@Composable
private fun getCategoryColor(categoryTitle: String): Color {
    return when (categoryTitle) {
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
}

@Composable
private fun TransactionInputItem(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        headlineContent = {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    innerTextField()
                },
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
            )
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
    onPaymentMethodMenuToggled: (Boolean) -> Unit = {},
    onLocationChange: (String) -> Unit = {},
    onAddPhotoClick: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val categoryColor = getCategoryColor(uiState.categoryTitle)

        OutlinedTextField(
            value = uiState.amount,
            onValueChange = onAmountChange,
            label = { Text(uiState.categoryTitle) },
            visualTransformation = AmountVisualTransformation(uiState.currencySymbol),
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
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.End)
        )

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                TransactionInputItem(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    placeholder = stringResource(R.string.transaction_notes_placeholder),
                    icon = Icons.Default.Description
                )

                HorizontalDivider()

                ListItem(
                    modifier = Modifier.clickable(onClick = onDateClick),
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    leadingContent = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    headlineContent = { Text(uiState.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)), fontWeight = FontWeight.SemiBold) },
                )

                HorizontalDivider()

                ExposedDropdownMenuBox(
                    expanded = uiState.isPaymentMethodMenuExpanded,
                    onExpandedChange = onPaymentMethodMenuToggled,
                ) {
                    ListItem(
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                            .clickable { onPaymentMethodMenuToggled(true) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            val icon = paymentMethodIcon(uiState.paymentMethod?.name)
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        headlineContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(uiState.paymentMethod?.name ?: "", fontWeight = FontWeight.SemiBold)
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.isPaymentMethodMenuExpanded)
                            }
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = uiState.isPaymentMethodMenuExpanded,
                        onDismissRequest = { onPaymentMethodMenuToggled(false) }
                    ) {
                        uiState.availablePaymentMethods.forEach { paymentMethod ->
                            val icon = paymentMethodIcon(paymentMethod.name)
                            DropdownMenuItem(
                                text = { Text(paymentMethod.name) },
                                leadingIcon = { Icon(icon, contentDescription = null) },
                                onClick = { onPaymentMethodChange(paymentMethod) }
                            )
                        }
                    }
                }

                HorizontalDivider()

                // TODO(klarkengkoy): Implement Google Maps to pin point Location in Transaction Screen.
                TransactionInputItem(
                    value = uiState.location,
                    onValueChange = onLocationChange,
                    placeholder = stringResource(R.string.transaction_location_placeholder),
                    icon = Icons.Default.LocationOn
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.imageUri != null) {
                val isPreview = LocalInspectionMode.current
                Card(
                    onClick = onAddPhotoClick,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isPreview) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Image preview placeholder")
                        }
                    } else {
                        AsyncImage(
                            model = uiState.imageUri,
                            contentDescription = "Transaction photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            } else {
                ElevatedCard(
                    onClick = onAddPhotoClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Add a photo") },
                        leadingContent = { Icon(Icons.Default.AddAPhoto, contentDescription = "Add a photo") },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }

        Button(
            onClick = onSave,
            enabled = uiState.amount.toDoubleOrNull() != 0.0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.transaction_save))
        }
    }
}

@Preview(showBackground = true, name = "Content Preview")
@Composable
fun TransactionContentPreview() {
    TripTrackTheme {
        TransactionContent(
            uiState = TransactionUiState(
                amount = "12345.67",
                description = "Dinner with friends",
                categoryTitle = "Restaurants",
                currencySymbol = "$",
                paymentMethod = PaymentMethod("Credit Card", 0),
                location = "Seoul, South Korea",
                availablePaymentMethods = listOf(
                    PaymentMethod("Cash", 0),
                    PaymentMethod("Credit Card", 0),
                )
            )
        )
    }
}

@Preview(showBackground = true, name = "Content Preview Empty")
@Composable
fun TransactionContentPreviewEmpty() {
    TripTrackTheme {
        TransactionContent(
            uiState = TransactionUiState(
                amount = "0",
                description = "",
                categoryTitle = "General",
                currencySymbol = "$",
                availablePaymentMethods = listOf(
                    PaymentMethod("Cash", 0),
                    PaymentMethod("Credit Card", 0),
                )
            )
        )
    }
}
