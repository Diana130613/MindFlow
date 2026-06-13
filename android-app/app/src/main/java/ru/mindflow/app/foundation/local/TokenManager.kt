package ru.mindflow.app.foundation.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("mindflow_prefs")

class TokenManager(private val context: Context) : ITokenStorage {

    companion object {
        private val KEY_ACCESS    = stringPreferencesKey("access_token")
        private val KEY_REFRESH   = stringPreferencesKey("refresh_token")
        private val KEY_EMAIL     = stringPreferencesKey("user_email")
        private val KEY_NAME      = stringPreferencesKey("user_name")
        private val KEY_JOIN_DATE = longPreferencesKey("join_date_millis")
    }

    override suspend fun saveTokens(
        accessToken: String, refreshToken: String,
        email: String, name: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ACCESS]  = accessToken
            prefs[KEY_REFRESH] = refreshToken
            prefs[KEY_EMAIL]   = email
            prefs[KEY_NAME]    = name
            if (!prefs.contains(KEY_JOIN_DATE)) {
                prefs[KEY_JOIN_DATE] = System.currentTimeMillis()
            }
        }
    }

    override suspend fun updateName(name: String) {
        context.dataStore.edit { it[KEY_NAME] = name }
    }

    override suspend fun getAccessToken(): String? =
        context.dataStore.data.map { it[KEY_ACCESS] }.firstOrNull()

    override suspend fun getRefreshToken(): String? =
        context.dataStore.data.map { it[KEY_REFRESH] }.firstOrNull()

    override suspend fun getUserEmail(): String? =
        context.dataStore.data.map { it[KEY_EMAIL] }.firstOrNull()

    override suspend fun getUserName(): String? =
        context.dataStore.data.map { it[KEY_NAME] }.firstOrNull()

    override suspend fun getJoinDateMillis(): Long? =
        context.dataStore.data.map { it[KEY_JOIN_DATE] }.firstOrNull()

    override suspend fun isLoggedIn(): Boolean = getAccessToken() != null

    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
