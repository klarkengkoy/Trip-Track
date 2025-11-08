package dev.klarkengkoy.triptrack.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.klarkengkoy.triptrack.data.UserDataStore
import dev.klarkengkoy.triptrack.data.local.TripDao
import dev.klarkengkoy.triptrack.data.local.TripTrackDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): UserDataStore {
        return UserDataStore(context)
    }
}
