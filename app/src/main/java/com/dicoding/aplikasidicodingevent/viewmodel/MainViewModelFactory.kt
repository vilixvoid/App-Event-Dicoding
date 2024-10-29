package com.dicoding.aplikasidicodingevent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import com.dicoding.aplikasidicodingevent.di.datastore.ReminderDataStore
import com.dicoding.aplikasidicodingevent.di.datastore.ThemeDataStore

class MainViewModelFactory(
    private val repository: EventRepository,
    private val themeDataStore: ThemeDataStore,
    private val reminderDataStore: ReminderDataStore
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, themeDataStore, reminderDataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
