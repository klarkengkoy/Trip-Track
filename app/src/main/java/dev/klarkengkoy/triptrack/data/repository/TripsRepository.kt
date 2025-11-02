package dev.klarkengkoy.triptrack.data.repository

import dev.klarkengkoy.triptrack.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripsRepository {

    fun getTrips(): Flow<List<Trip>>

    suspend fun addTrip(trip: Trip)

    suspend fun getTrip(tripId: String): Trip?

    suspend fun deleteTrip(tripId: String)

    suspend fun syncTrips()
}
