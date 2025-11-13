package dev.klarkengkoy.triptrack.data

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import dev.klarkengkoy.triptrack.model.TransactionType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class Converters {

    // region Room Converters
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun toTransactionType(value: String) = enumValueOf<TransactionType>(value)

    @TypeConverter
    fun fromTransactionType(value: TransactionType) = value.name
    // endregion


    // region Firestore Converters
    fun fromFirebaseTimestamp(timestamp: Timestamp?): LocalDate? {
        return timestamp?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
    }

    fun toFirebaseTimestamp(date: LocalDate?): Timestamp? {
        return date?.let { Timestamp(it.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond, 0) }
    }
    // endregion
}
