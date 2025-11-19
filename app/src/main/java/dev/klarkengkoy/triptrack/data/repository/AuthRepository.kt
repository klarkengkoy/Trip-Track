package dev.klarkengkoy.triptrack.data.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserSignedIn: Flow<Boolean>
    fun getUserName(): String?
    fun getUserEmail(): String?
    suspend fun signOut()
}
