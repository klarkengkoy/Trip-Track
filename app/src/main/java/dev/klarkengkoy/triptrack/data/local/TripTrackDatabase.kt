package dev.klarkengkoy.triptrack.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.klarkengkoy.triptrack.model.Trip

@Database(entities = [Trip::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TripTrackDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
