package dev.klarkengkoy.triptrack.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        val ACTIVE_TRIP_ID_KEY = stringPreferencesKey("active_trip_id")
    }

    suspend fun saveUser(name: String, email: String) {
        dataStore.edit { preferences ->
            // Elvis operator to handle null name from some providers
            preferences[USER_NAME_KEY] = name
            preferences[USER_EMAIL_KEY] = email
        }
    }

    suspend fun setActiveTripId(tripId: String?) {
        dataStore.edit { preferences ->
            if (tripId != null) {
                preferences[ACTIVE_TRIP_ID_KEY] = tripId
            } else {
                preferences.remove(ACTIVE_TRIP_ID_KEY)
            }
        }
    }

    val userNameFlow: Flow<String?> = dataStore.data.map {
        it[USER_NAME_KEY]
    }

    val userEmailFlow: Flow<String?> = dataStore.data.map {
        it[USER_EMAIL_KEY]
    }

    val activeTripIdFlow: Flow<String?> = dataStore.data.map {
        it[ACTIVE_TRIP_ID_KEY]
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
