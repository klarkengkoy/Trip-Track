package dev.klarkengkoy.triptrack.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val isUserSignedIn: Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override fun getUserName(): String? = firebaseAuth.currentUser?.displayName

    override fun getUserEmail(): String? = firebaseAuth.currentUser?.email

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
