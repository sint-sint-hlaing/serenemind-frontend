package com.serenemind.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("auth")

class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        val prefs = context.dataStore.data.first()
        return prefs[TOKEN_KEY]
    }

    suspend fun clearToken() {
        context.dataStore.edit {
            it.clear()
        }
    }
}