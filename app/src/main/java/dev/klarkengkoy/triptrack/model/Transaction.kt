package dev.klarkengkoy.triptrack.model

import androidx.annotation.DrawableRes
import java.time.LocalDate
import java.util.UUID

enum class TransactionType {
    EXPENSE,
    INCOME
}

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)

data class Category(
    val name: String,
    @DrawableRes val iconRes: Int
)

data class PaymentMethod(
    val name: String,
    @DrawableRes val iconRes: Int
)

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val notes: String,
    val amount: Double,
    val date: LocalDate,
    val category: Category,
    val paymentMethod: PaymentMethod,
    val location: Coordinates? = null,
    val excludeFromBudget: Boolean = false,
    val type: TransactionType
)
