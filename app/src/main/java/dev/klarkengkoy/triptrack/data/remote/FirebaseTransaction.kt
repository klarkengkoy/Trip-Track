package dev.klarkengkoy.triptrack.data.remote

import com.google.firebase.Timestamp
import dev.klarkengkoy.triptrack.data.Converters
import dev.klarkengkoy.triptrack.model.Category
import dev.klarkengkoy.triptrack.model.Coordinates
import dev.klarkengkoy.triptrack.model.PaymentMethod
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.TransactionType

data class FirebaseTransaction(
    val id: String = "",
    val tripId: String = "",
    val notes: String? = null,
    val amount: Double = 0.0,
    val date: Timestamp? = null,
    val category: Category = Category("", 0),
    val paymentMethod: PaymentMethod = PaymentMethod("", 0),
    val location: Coordinates? = null,
    val imageUri: String? = null,
    val excludeFromBudget: Boolean = false,
    val type: String = TransactionType.EXPENSE.name,
    val isDeleted: Boolean = false
) {
    companion object {
        fun fromTransaction(transaction: Transaction): FirebaseTransaction {
            val converters = Converters()
            return FirebaseTransaction(
                id = transaction.id,
                tripId = transaction.tripId,
                notes = transaction.notes,
                amount = transaction.amount,
                date = converters.toFirebaseTimestamp(transaction.date),
                category = transaction.category,
                paymentMethod = transaction.paymentMethod,
                location = transaction.location,
                imageUri = transaction.imageUri,
                excludeFromBudget = transaction.excludeFromBudget,
                type = transaction.type.name
            )
        }
    }
}
