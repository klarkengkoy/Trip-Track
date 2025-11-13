package dev.klarkengkoy.triptrack.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.klarkengkoy.triptrack.data.Converters
import dev.klarkengkoy.triptrack.data.remote.FirebaseTrip
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "trips")
@TypeConverters(Converters::class)
data class Trip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val currency: String = "",
    val isCurrencyCustom: Boolean = false,
    val imageUri: String? = null,
    val imageOffsetX: Float = 0f,
    val imageOffsetY: Float = 0f,
    val imageScale: Float = 1f,
    @JvmField
    val startDate: LocalDate? = null,
    @JvmField
    val endDate: LocalDate? = null,
    val dailyBudget: Double? = null,
    val totalBudget: Double? = null,
    val isActive: Boolean = false,
    val isDeleted: Boolean = false,
    val dateCreated: LocalDate = LocalDate.now()
) {
    companion object {
        fun fromFirebaseTrip(firebaseTrip: FirebaseTrip): Trip {
            val converters = Converters()
            return Trip(
                id = firebaseTrip.id,
                name = firebaseTrip.name,
                currency = firebaseTrip.currency,
                isCurrencyCustom = firebaseTrip.isCurrencyCustom,
                imageUri = firebaseTrip.imageUri,
                imageOffsetX = firebaseTrip.imageOffsetX,
                imageOffsetY = firebaseTrip.imageOffsetY,
                imageScale = firebaseTrip.imageScale,
                startDate = converters.fromFirebaseTimestamp(firebaseTrip.startDate),
                endDate = converters.fromFirebaseTimestamp(firebaseTrip.endDate),
                dailyBudget = firebaseTrip.dailyBudget,
                totalBudget = firebaseTrip.totalBudget,
                isActive = firebaseTrip.isActive,
                isDeleted = firebaseTrip.isDeleted,
                dateCreated = converters.fromFirebaseTimestamp(firebaseTrip.dateCreated) ?: LocalDate.now()
            )
        }
    }
}
