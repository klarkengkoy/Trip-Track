package dev.klarkengkoy.triptrack.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColors(
    val accommodation: Color,
    val activities: Color,
    val drinks: Color,
    val entertainment: Color,
    val feesAndCharges: Color,
    val flights: Color,
    val general: Color,
    val giftsAndSouvenirs: Color,
    val groceries: Color,
    val insurance: Color,
    val laundry: Color,
    val restaurants: Color,
    val shopping: Color,
    val toursAndEntry: Color,
    val transportation: Color,
    val salary: Color,
    val gift: Color,
    val other: Color
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        accommodation = Color.Unspecified,
        activities = Color.Unspecified,
        drinks = Color.Unspecified,
        entertainment = Color.Unspecified,
        feesAndCharges = Color.Unspecified,
        flights = Color.Unspecified,
        general = Color.Unspecified,
        giftsAndSouvenirs = Color.Unspecified,
        groceries = Color.Unspecified,
        insurance = Color.Unspecified,
        laundry = Color.Unspecified,
        restaurants = Color.Unspecified,
        shopping = Color.Unspecified,
        toursAndEntry = Color.Unspecified,
        transportation = Color.Unspecified,
        salary = Color.Unspecified,
        gift = Color.Unspecified,
        other = Color.Unspecified
    )
}

val LightCustomColors = CustomColors(
    accommodation = Color(0xFFE57373),
    activities = Color(0xFF81C784),
    drinks = Color(0xFF64B5F6),
    entertainment = Color(0xFFFFF176),
    feesAndCharges = Color(0xFF9575CD),
    flights = Color(0xFF4DB6AC),
    general = Color(0xFFBDBDBD),
    giftsAndSouvenirs = Color(0xFFFFB74D),
    groceries = Color(0xFFF06292),
    insurance = Color(0xFF7986CB),
    laundry = Color(0xFFA1887F),
    restaurants = Color(0xFFDCE775),
    shopping = Color(0xFFBA68C8),
    toursAndEntry = Color(0xFF4DD0E1),
    transportation = Color(0xFF8BC34A),
    salary = Color(0xFF4CAF50),
    gift = Color(0xFFE91E63),
    other = Color(0xFF9E9E9E)
)

val DarkCustomColors = CustomColors(
    // For this example, I'm using the same colors.
    // In a real app, you'd define different, darker shades here.
    accommodation = Color(0xFFE57373),
    activities = Color(0xFF81C784),
    drinks = Color(0xFF64B5F6),
    entertainment = Color(0xFFFFF176),
    feesAndCharges = Color(0xFF9575CD),
    flights = Color(0xFF4DB6AC),
    general = Color(0xFFBDBDBD),
    giftsAndSouvenirs = Color(0xFFFFB74D),
    groceries = Color(0xFFF06292),
    insurance = Color(0xFF7986CB),
    laundry = Color(0xFFA1887F),
    restaurants = Color(0xFFDCE775),
    shopping = Color(0xFFBA68C8),
    toursAndEntry = Color(0xFF4DD0E1),
    transportation = Color(0xFF8BC34A),
    salary = Color(0xFF4CAF50),
    gift = Color(0xFFE91E63),
    other = Color(0xFF9E9E9E)
)

@Suppress("unused")
val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current