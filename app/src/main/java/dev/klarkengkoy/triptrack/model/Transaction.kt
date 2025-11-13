package dev.klarkengkoy.triptrack.model

import androidx.annotation.DrawableRes
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.klarkengkoy.triptrack.data.Converters
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
    @field:DrawableRes val iconRes: Int
)

data class PaymentMethod(
    val name: String,
    @field:DrawableRes val iconRes: Int
)

@Entity(tableName = "transactions")
@TypeConverters(Converters::class)
data class Transaction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val notes: String?,
    val amount: Double,
    val date: LocalDate,
    @Embedded(prefix = "category_")
    val category: Category,
    @Embedded(prefix = "payment_")
    val paymentMethod: PaymentMethod,
    @Embedded(prefix = "location_")
    val location: Coordinates? = null,
    val excludeFromBudget: Boolean = false,
    val type: TransactionType
)
