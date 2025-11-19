package dev.klarkengkoy.triptrack.data.repository

import dev.klarkengkoy.triptrack.model.Transaction
import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripsRepository {

    fun getTrips(): Flow<List<Trip>>

    suspend fun addTrip(trip: Trip)

    suspend fun updateTrip(trip: Trip)

    suspend fun getTrip(tripId: String): Trip?

    suspend fun deleteTrip(tripId: String)

    suspend fun deleteTrips(tripIds: Set<String>)

    suspend fun syncTrips()

    suspend fun addTransaction(transaction: Transaction)

    fun getTransactions(tripId: String): Flow<List<Transaction>>

    suspend fun setActiveTrip(tripId: String, isActive: Boolean)

    fun getActiveTrip(): Flow<Trip?>
}
