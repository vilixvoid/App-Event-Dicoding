package com.dicoding.aplikasidicodingevent.di.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.reminderDataStore: DataStore<Preferences> by preferencesDataStore(name = "reminder_setting")

class ReminderDataStore(private val context: Context) {
    private val reminderKey = booleanPreferencesKey("is_reminder_active")

    fun getReminderSetting(): Flow<Boolean> {
        return context.reminderDataStore.data.map { preferences ->
            preferences[reminderKey] ?: false
        }
    }

    suspend fun saveReminderSetting(isReminderActive: Boolean) {
        context.reminderDataStore.edit { preferences ->
            preferences[reminderKey] = isReminderActive
        }
    }
}
