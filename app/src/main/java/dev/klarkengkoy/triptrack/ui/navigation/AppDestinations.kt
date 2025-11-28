package dev.klarkengkoy.triptrack.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// region Login Graph
@Serializable
object Login : NavKey

@Serializable
data class Legal(val clickedText: String) : NavKey
// endregion

// region Main Tabs
@Serializable
data class Trips(val tripId: String? = null) : NavKey

@Serializable
object Dashboard : NavKey

@Serializable
object Media : NavKey

@Serializable
object Maps : NavKey

@Serializable
object Settings : NavKey
// endregion

// region Add Trip Wizard
@Serializable
object AddTripName : NavKey

@Serializable
object AddTripCurrency : NavKey

@Serializable
object AddTripDates : NavKey

@Serializable
object AddTripBudget : NavKey

@Serializable
object AddTripPhoto : NavKey

@Serializable
object AddTripSummary : NavKey
// endregion

// region Edit Trip Wizard
@Serializable
data class EditTripName(val tripId: String) : NavKey

@Serializable
object EditTripCurrency : NavKey

@Serializable
object EditTripDates : NavKey

@Serializable
object EditTripBudget : NavKey

@Serializable
object EditTripPhoto : NavKey // Added missing NavKey

@Serializable
object EditTripSummary : NavKey
// endregion

// region Currency Selection (Shared)
@Serializable
object CurrencyList : NavKey
// endregion

// region Add Transaction Wizard
@Serializable
data class AddTransactionCategory(val tripId: String) : NavKey

@Serializable
data class AddTransactionDetails(
    val tripId: String,
    val category: String
) : NavKey
// endregion

// region Edit Transaction Wizard
@Serializable
data class EditTransactionDetails(val transactionId: String) : NavKey

@Serializable
data class EditTransactionCategory(val transactionId: String) : NavKey
// endregion
