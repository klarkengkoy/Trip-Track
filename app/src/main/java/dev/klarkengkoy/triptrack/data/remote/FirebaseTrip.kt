package dev.klarkengkoy.triptrack.data.remote

import com.google.firebase.Timestamp
import dev.klarkengkoy.triptrack.data.Converters
import dev.klarkengkoy.triptrack.model.Trip

data class FirebaseTrip(
    val id: String = "",
    val name: String = "",
    val currency: String = "",
    val isCurrencyCustom: Boolean = false,
    val imageUri: String? = null,
    val imageOffsetX: Float = 0f,
    val imageOffsetY: Float = 0f,
    val imageScale: Float = 1f,
    val startDate: Timestamp? = null,
    val endDate: Timestamp? = null,
    val dailyBudget: Double? = null,
    val totalBudget: Double? = null,
    val isActive: Boolean = false,
    val isDeleted: Boolean = false,
    val dateCreated: Timestamp = Timestamp.now()
) {
    companion object {
        fun fromTrip(trip: Trip): FirebaseTrip {
            val converters = Converters()
            return FirebaseTrip(
                id = trip.id,
                name = trip.name,
                currency = trip.currency,
                isCurrencyCustom = trip.isCurrencyCustom,
                imageUri = trip.imageUri,
                imageOffsetX = trip.imageOffsetX,
                imageOffsetY = trip.imageOffsetY,
                imageScale = trip.imageScale,
                startDate = converters.toFirebaseTimestamp(trip.startDate),
                endDate = converters.toFirebaseTimestamp(trip.endDate),
                dailyBudget = trip.dailyBudget,
                totalBudget = trip.totalBudget,
                isActive = trip.isActive,
                isDeleted = trip.isDeleted,
                dateCreated = converters.toFirebaseTimestamp(trip.dateCreated) ?: Timestamp.now()
            )
        }
    }
}
