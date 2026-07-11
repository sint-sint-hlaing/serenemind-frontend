package com.serenemind.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore("theme_settings")

class ThemeManager(private val context: Context) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LAST_CELEBRATED_STREAK_KEY = intPreferencesKey("last_celebrated_streak")
    }

    val isDarkMode: Flow<Boolean?> = context.themeDataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY]
    }

    val lastCelebratedStreak: Flow<Int> = context.themeDataStore.data.map { prefs ->
        prefs[LAST_CELEBRATED_STREAK_KEY] ?: -1
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun saveLastCelebratedStreak(streak: Int) {
        context.themeDataStore.edit { prefs ->
            prefs[LAST_CELEBRATED_STREAK_KEY] = streak
        }
    }
}
