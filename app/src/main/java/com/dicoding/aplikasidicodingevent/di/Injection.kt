package com.dicoding.aplikasidicodingevent.di

import android.content.Context
import com.dicoding.aplikasidicodingevent.data.local.EventDatabase
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import com.dicoding.aplikasidicodingevent.data.repository.EventRepositoryImpl
import com.dicoding.aplikasidicodingevent.di.datastore.ReminderDataStore
import com.dicoding.aplikasidicodingevent.di.datastore.ThemeDataStore

object Injection {
    private var repository: EventRepository? = null

    fun provideRepository(context: Context): EventRepository {
        return repository ?: synchronized(this) {
            val instance = EventRepositoryImpl(EventDatabase.getInstance(context).eventDao())
            repository = instance
            instance
        }
    }

    fun provideThemeDataStore(context: Context): ThemeDataStore {
        return ThemeDataStore(context.applicationContext)
    }

    fun provideReminderDataStore(context: Context): ReminderDataStore {
        return ReminderDataStore(context.applicationContext)
    }
}
