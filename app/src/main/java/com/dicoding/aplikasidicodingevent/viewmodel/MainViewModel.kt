package com.dicoding.aplikasidicodingevent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.aplikasidicodingevent.data.local.EventEntity
import com.dicoding.aplikasidicodingevent.data.repository.EventRepository
import com.dicoding.aplikasidicodingevent.di.datastore.ReminderDataStore
import com.dicoding.aplikasidicodingevent.di.datastore.ThemeDataStore
import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import com.dicoding.aplikasidicodingevent.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainViewModel(
    private val repository: EventRepository,
    private val pref: ThemeDataStore,
    private val reminderPref: ReminderDataStore
) : ViewModel() {

    private val _activeEvents = MutableLiveData<List<ListEventsItem>>()
    val activeEvents: LiveData<List<ListEventsItem>> = _activeEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        viewModelScope.launch {
            fetchEvents(1) // Fetch active events
            fetchEvents(0) // Fetch finished events
        }
    }

    private suspend fun fetchEvents(active: Int) {
        _isLoading.postValue(true)
        _errorMessage.postValue(null)
        try {
            val response = withContext(Dispatchers.IO) {
                ApiConfig.create().getEvents(active)
            }
            when (active) {
                1 -> _activeEvents.postValue(response.listEvents)
                0 -> _finishedEvents.postValue(response.listEvents)
            }
        } catch (e: IOException) {
            _errorMessage.postValue("Gagal memuat data. Periksa koneksi internet Anda.")
        } catch (e: Exception) {
            _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            Log.e("MainViewModel", "Error fetching events", e)
        } finally {
            _isLoading.postValue(false)
        }
    }

    fun searchEvents(query: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _errorMessage.postValue(null)
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.create().searchEvents(query = query)
                }
                _searchResults.postValue(response.listEvents)
            } catch (e: Exception) {
                _errorMessage.postValue("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }


    fun insertEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.insertEvent(event)
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun isEventFavorite(id: Int) = repository.isEventFavorite(id).asLiveData()

    fun getFavoriteEvents() = repository.getFavoriteEvents().asLiveData()

    fun getThemeSetting() = pref.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getReminderSetting() = reminderPref.getReminderSetting().asLiveData()

    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            reminderPref.saveReminderSetting(isReminderActive)
        }
    }
}
