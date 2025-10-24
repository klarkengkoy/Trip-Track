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
    }

    suspend fun saveUser(name: String, email: String) {
        dataStore.edit { preferences ->
            // Elvis operator to handle null name from some providers
            preferences[USER_NAME_KEY] = name ?: ""
            preferences[USER_EMAIL_KEY] = email
        }
    }

    val userNameFlow: Flow<String?> = dataStore.data.map {
        it[USER_NAME_KEY]
    }

    val userEmailFlow: Flow<String?> = dataStore.data.map {
        it[USER_EMAIL_KEY]
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}