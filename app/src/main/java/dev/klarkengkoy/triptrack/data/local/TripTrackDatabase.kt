package dev.klarkengkoy.triptrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.klarkengkoy.triptrack.data.Converters
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.Trip

@Database(entities = [Trip::class, Transaction::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TripTrackDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
