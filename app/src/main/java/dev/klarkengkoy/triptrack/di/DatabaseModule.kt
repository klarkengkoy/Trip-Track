package dev.klarkengkoy.triptrack.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.klarkengkoy.triptrack.data.local.TripDao
import dev.klarkengkoy.triptrack.data.local.TripTrackDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTripTrackDatabase(@ApplicationContext context: Context): TripTrackDatabase {
        return Room.databaseBuilder(
            context,
            TripTrackDatabase::class.java,
            "triptrack_database"
        ).build()
    }

    @Provides
    fun provideTripDao(database: TripTrackDatabase): TripDao {
        return database.tripDao()
    }

}
