package com.dicoding.aplikasidicodingevent.di.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_setting")

class ThemeDataStore(private val context: Context) {

    private val themeKey = booleanPreferencesKey("is_dark_theme")

    fun getThemeSetting(): Flow<Boolean> {
        return context.themeDataStore.data.map { preferences ->
            preferences[themeKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkTheme: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[themeKey] = isDarkTheme
        }
    }
}
