package dev.klarkengkoy.triptrack.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Attractions
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TransactionCategory(val title: String, val icon: ImageVector, val route: String) {
    object Accommodation : TransactionCategory("Accommodation", Icons.Default.Hotel, "accommodation")
    object Activities : TransactionCategory("Activities", Icons.AutoMirrored.Filled.DirectionsRun, "activities")
    object Drinks : TransactionCategory("Drinks", Icons.Default.LocalBar, "drinks")
    object Entertainment : TransactionCategory("Entertainment", Icons.Default.LocalActivity, "entertainment")
    object FeesAndCharges : TransactionCategory("Fees & Charges", Icons.Default.Receipt, "fees_and_charges")
    object Flights : TransactionCategory("Flights", Icons.Default.Flight, "flights")
    object General : TransactionCategory("General", Icons.Default.CardTravel, "general")
    object GiftsAndSouvenirs : TransactionCategory("Gifts & Souvenirs", Icons.Default.CardGiftcard, "gifts_and_souvenirs")
    object Groceries : TransactionCategory("Groceries", Icons.Default.LocalGroceryStore, "groceries")
    object Insurance : TransactionCategory("Insurance", Icons.Default.HealthAndSafety, "insurance")
    object Laundry : TransactionCategory("Laundry", Icons.Default.LocalLaundryService, "laundry")
    object Restaurants : TransactionCategory("Restaurants", Icons.Default.Restaurant, "restaurants")
    object Shopping : TransactionCategory("Shopping", Icons.Default.ShoppingCart, "shopping")
    object ToursAndEntry : TransactionCategory("Tours & Entry", Icons.Default.Attractions, "tours_and_entry")
    object Transportation : TransactionCategory("Transportation", Icons.Default.DirectionsBus, "transportation")

    companion object {
        fun fromRoute(route: String?): TransactionCategory? {
            return when (route) {
                Accommodation.route -> Accommodation
                Activities.route -> Activities
                Drinks.route -> Drinks
                Entertainment.route -> Entertainment
                FeesAndCharges.route -> FeesAndCharges
                Flights.route -> Flights
                General.route -> General
                GiftsAndSouvenirs.route -> GiftsAndSouvenirs
                Groceries.route -> Groceries
                Insurance.route -> Insurance
                Laundry.route -> Laundry
                Restaurants.route -> Restaurants
                Shopping.route -> Shopping
                ToursAndEntry.route -> ToursAndEntry
                Transportation.route -> Transportation
                else -> null
            }
        }

        val categories = listOf(
            Accommodation,
            Activities,
            Drinks,
            Entertainment,
            FeesAndCharges,
            Flights,
            General,
            GiftsAndSouvenirs,
            Groceries,
            Insurance,
            Laundry,
            Restaurants,
            Shopping,
            ToursAndEntry,
            Transportation
        ).sortedBy { it.title }
    }
}
