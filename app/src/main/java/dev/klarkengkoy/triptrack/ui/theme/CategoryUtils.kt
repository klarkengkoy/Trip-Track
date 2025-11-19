package dev.klarkengkoy.triptrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import dev.klarkengkoy.triptrack.model.IncomeCategory
import dev.klarkengkoy.triptrack.model.TransactionCategory

@Composable
@ReadOnlyComposable
fun getCategoryColor(categoryName: String): Color {
    val customColors = MaterialTheme.customColors
    return when (categoryName) {
        TransactionCategory.Accommodation.title -> customColors.accommodation
        TransactionCategory.Activities.title -> customColors.activities
        TransactionCategory.Drinks.title -> customColors.drinks
        TransactionCategory.Entertainment.title -> customColors.entertainment
        TransactionCategory.FeesAndCharges.title -> customColors.feesAndCharges
        TransactionCategory.Flights.title -> customColors.flights
        TransactionCategory.General.title -> customColors.general
        TransactionCategory.GiftsAndSouvenirs.title -> customColors.giftsAndSouvenirs
        TransactionCategory.Groceries.title -> customColors.groceries
        TransactionCategory.Insurance.title -> customColors.insurance
        TransactionCategory.Laundry.title -> customColors.laundry
        TransactionCategory.Restaurants.title -> customColors.restaurants
        TransactionCategory.Shopping.title -> customColors.shopping
        TransactionCategory.ToursAndEntry.title -> customColors.toursAndEntry
        TransactionCategory.Transportation.title -> customColors.transportation
        IncomeCategory.Salary.title -> customColors.salary
        IncomeCategory.Gifts.title -> customColors.gift
        IncomeCategory.OtherIncome.title -> customColors.other
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
