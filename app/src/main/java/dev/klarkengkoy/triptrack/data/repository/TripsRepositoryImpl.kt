package dev.klarkengkoy.triptrack.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import dev.klarkengkoy.triptrack.data.local.TripDao
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TripsRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val firestore: FirebaseFirestore
) : TripsRepository {

    override fun getTrips(): Flow<List<Trip>> {
        // For offline-first, we always return the flow from the local database.
        // TODO: Add a mechanism to sync data from Firestore to the local DB.
        return tripDao.getTrips()
    }

    override suspend fun addTrip(trip: Trip) {
        // First, save to the local database for immediate offline access.
        tripDao.insertTrip(trip)
        // Then, save to Firestore for backup and sync across devices.
        firestore.collection("trips").document(trip.id).set(trip)
    }

    override suspend fun getTrip(tripId: String): Trip? {
        return tripDao.getTripById(tripId)
    }

    override suspend fun deleteTrip(tripId: String) {
        // Delete from local DB first
        tripDao.deleteTrip(tripId)
        // Then delete from Firestore
        firestore.collection("trips").document(tripId).delete()
    }
}