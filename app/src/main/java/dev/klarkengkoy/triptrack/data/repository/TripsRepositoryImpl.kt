package dev.klarkengkoy.triptrack.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dev.klarkengkoy.triptrack.data.local.TripDao
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "TripsRepositoryImpl"

class TripsRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : TripsRepository {

    /**
     * A reference to the user-specific trips collection in Firestore.
     * Returns null if no user is logged in.
     */
    private val userTripsCollection: CollectionReference?
        get() = auth.currentUser?.uid?.let {
            firestore.collection("users").document(it).collection("trips")
        }

    override fun getTrips(): Flow<List<Trip>> {
        // For offline-first, we always return the flow from the local database.
        return tripDao.getTrips()
    }

    override suspend fun addTrip(trip: Trip) {
        // First, save to the local database for immediate offline access.
        tripDao.insertTrip(trip)
        // Then, save to Firestore for backup and sync across devices.
        userTripsCollection?.document(trip.id)?.set(trip)?.await()
    }

    override suspend fun getTrip(tripId: String): Trip? {
        return tripDao.getTripById(tripId)
    }

    override suspend fun deleteTrip(tripId: String) {
        // "Soft delete" the trip in the local database by marking it for deletion.
        tripDao.getTripById(tripId)?.let {
            tripDao.insertTrip(it.copy(isDeleted = true))
        }
    }

    override suspend fun deleteTrips(tripIds: Set<String>) {
        // Perform a "soft delete" locally first for immediate UI update
        tripIds.forEach { tripId ->
            tripDao.getTripById(tripId)?.let {
                tripDao.insertTrip(it.copy(isDeleted = true))
            }
        }
        // Immediately attempt to sync the deletion to the remote backend.
        syncTrips()
    }

    override suspend fun syncTrips() {
        try {
            val collection = userTripsCollection ?: return

            // 1. Handle local deletions
            val deletedTrips = tripDao.getDeletedTrips()
            if (deletedTrips.isNotEmpty()) {
                val batch = firestore.batch()
                deletedTrips.forEach { trip ->
                    batch.delete(collection.document(trip.id))
                }
                batch.commit().await()
                deletedTrips.forEach { trip ->
                    tripDao.deleteTrip(trip.id) // Permanent delete from local DB
                }
                Log.d(TAG, "Uploaded deletions for ${deletedTrips.size} trips.")
            }

            // 2. Handle local additions/updates
            val localTrips = tripDao.getTrips().first() // Get current list of non-deleted trips
            val remoteTripIds = collection.get().await().documents.map { it.id }.toSet()

            localTrips.forEach { localTrip ->
                if (localTrip.id !in remoteTripIds) {
                    collection.document(localTrip.id).set(localTrip).await()
                    Log.d(TAG, "Uploaded new local trip: ${localTrip.id}")
                }
            }

            // 3. Download all remote trips to get the latest state
            val remoteTrips = collection.get().await().toObjects<Trip>()
            remoteTrips.forEach { trip ->
                tripDao.insertTrip(trip)
            }
            Log.d(TAG, "Finished downloading remote trips. Local DB is now in sync.")

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing trips", e)
            // This will fail gracefully if there is no internet connection,
            // and will be re-attempted the next time the user logs in.
        }
    }
}