package dev.klarkengkoy.triptrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import dev.klarkengkoy.triptrack.data.UserDataStore
import dev.klarkengkoy.triptrack.data.Converters
import dev.klarkengkoy.triptrack.data.local.TripDao
import dev.klarkengkoy.triptrack.data.remote.FirebaseTransaction
import dev.klarkengkoy.triptrack.data.remote.FirebaseTrip
import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TripsRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userDataStore: UserDataStore
) : TripsRepository {

    private val userTripsCollection: CollectionReference?
        get() = auth.currentUser?.uid?.let {
            firestore.collection("users").document(it).collection("trips")
        }

    private fun getTripTransactionsCollection(tripId: String): CollectionReference? {
        return userTripsCollection?.document(tripId)?.collection("transactions")
    }

    override fun getTrips(): Flow<List<Trip>> {
        return tripDao.getTrips()
    }

    override suspend fun addTrip(trip: Trip) {
        tripDao.insertTrip(trip)
        userTripsCollection?.document(trip.id)?.set(FirebaseTrip.fromTrip(trip))?.await()
    }

    override suspend fun updateTrip(trip: Trip) {
        tripDao.insertTrip(trip)
        userTripsCollection?.document(trip.id)?.set(FirebaseTrip.fromTrip(trip))?.await()
    }

    override suspend fun getTrip(tripId: String): Trip? {
        return tripDao.getTripById(tripId)
    }

    override suspend fun deleteTrip(tripId: String) {
        tripDao.getTripById(tripId)?.let {
            tripDao.insertTrip(it.copy(isDeleted = true))
        }
    }

    override suspend fun deleteTrips(tripIds: Set<String>) {
        tripIds.forEach { tripId ->
            tripDao.getTripById(tripId)?.let {
                tripDao.insertTrip(it.copy(isDeleted = true))
            }
        }
        syncTrips()
    }

    override suspend fun syncTrips() {
        val collection = userTripsCollection ?: return

        syncDeletions(collection)
        uploadNewLocalTrips(collection)
        fetchRemoteTrips(collection)
    }

    override suspend fun addTransaction(transaction: Transaction) {
        tripDao.insertTransaction(transaction)
        getTripTransactionsCollection(transaction.tripId)?.document(transaction.id)?.set(FirebaseTransaction.fromTransaction(transaction))?.await()
    }

    override fun getTransactions(tripId: String): Flow<List<Transaction>> {
        return tripDao.getTransactions(tripId)
    }

    override suspend fun setActiveTrip(tripId: String, isActive: Boolean) {
        val idToSave = if (isActive) tripId else null
        userDataStore.setActiveTripId(idToSave)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getActiveTrip(): Flow<Trip?> {
        return userDataStore.activeTripIdFlow.flatMapLatest { tripId ->
            if (tripId != null) {
                tripDao.getTripFlowById(tripId)
            } else {
                flowOf(null)
            }
        }
    }

    private suspend fun syncDeletions(collection: CollectionReference) {
        val deletedTrips = tripDao.getDeletedTrips()
        if (deletedTrips.isNotEmpty()) {
            val batch = firestore.batch()
            deletedTrips.forEach { trip ->
                batch.delete(collection.document(trip.id))
            }
            batch.commit().await()
            deletedTrips.forEach { trip ->
                tripDao.deleteTrip(trip.id)
            }
        }
    }

    private suspend fun uploadNewLocalTrips(collection: CollectionReference) {
        val localTrips = tripDao.getTrips().first()
        val remoteTripIds = collection.get().await().documents.map { it.id }.toSet()

        localTrips.forEach { localTrip ->
            if (localTrip.id !in remoteTripIds) {
                collection.document(localTrip.id).set(FirebaseTrip.fromTrip(localTrip)).await()
            }
        }
    }

    private suspend fun fetchRemoteTrips(collection: CollectionReference) {
        val remoteTrips = collection.get().await().toObjects<FirebaseTrip>()
        remoteTrips.forEach { firebaseTrip ->
            val newTrip = Trip.fromFirebaseTrip(firebaseTrip)
            tripDao.insertTrip(newTrip)

            // Fetch transactions for this trip
            val transactionsCollection = getTripTransactionsCollection(newTrip.id)
            val remoteTransactions = transactionsCollection?.get()?.await()?.toObjects<FirebaseTransaction>()
            remoteTransactions?.forEach { firebaseTransaction ->
                val newTransaction = Converters().fromFirebaseTransaction(firebaseTransaction)
                if (newTransaction != null) {
                     tripDao.insertTransaction(newTransaction)
                }
            }
        }
    }
}
