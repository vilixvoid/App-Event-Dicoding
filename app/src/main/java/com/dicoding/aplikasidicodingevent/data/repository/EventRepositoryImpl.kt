package com.dicoding.aplikasidicodingevent.data.repository

import android.util.Log
import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import com.dicoding.aplikasidicodingevent.data.local.EventDao
import com.dicoding.aplikasidicodingevent.data.local.EventEntity
import com.dicoding.aplikasidicodingevent.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class EventRepositoryImpl(
    private val eventDao: EventDao
) : EventRepository {

    override fun getActiveEvents(): Flow<List<ListEventsItem>> {
        return flow {
            try {
                val response = ApiConfig.create().getEvents(active = 1)
                emit(response.listEvents)
            } catch (e: IOException) {
                throw ApiException("Network error: ${e.message}")
            } catch (e: Exception) {
                throw ApiException("Error getting active events: ${e.message}")
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getFinishedEvents(): Flow<List<ListEventsItem>> {
        return flow {
            try {
                val response = ApiConfig.create().getEvents(active = 0)
                emit(response.listEvents)
            } catch (e: IOException) {
                Log.e("EventRepository", "Network error when fetching finished events", e)
                throw ApiException("Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("EventRepository", "Unexpected error when fetching finished events", e)
                throw ApiException("Error getting finished events: ${e.message}")
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun searchEvents(query: String): Flow<List<ListEventsItem>> {
        return flow {
            try {
                val response = ApiConfig.create().searchEvents(query = query)
                emit(response.listEvents)
            } catch (e: IOException) {
                throw ApiException("Network error: ${e.message}")
            } catch (e: Exception) {
                throw ApiException("Error searching events: ${e.message}")
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getNearestActiveEvent(): Flow<ListEventsItem?> {
        return flow {
            try {
                val response = ApiConfig.create().getEvents(active = 1, limit = 1)
                emit(response.listEvents.firstOrNull() ?: ListEventsItem())
            } catch (e: IOException) {
                throw ApiException("Network error: ${e.message}")
            } catch (e: Exception) {
                throw ApiException("Error getting nearest active event: ${e.message}")
            }
        }.flowOn(Dispatchers.IO)
    }


    override fun getFavoriteEvents(): Flow<List<EventEntity>> {
        return eventDao.getFavoriteEvents()
    }

    override fun isEventFavorite(id: Int): Flow<Boolean> {
        return eventDao.isEventFavorite(id)
    }

    override suspend fun insertEvent(event: EventEntity) {
        withContext(Dispatchers.IO) {
            try {
                eventDao.insertEvent(event)
            } catch (e: Exception) {
                throw ApiException("Error inserting event: ${e.message}")
            }
        }
    }

    override suspend fun deleteEvent(event: EventEntity) {
        withContext(Dispatchers.IO) {
            try {
                eventDao.deleteEvent(event)
            } catch (e: Exception) {
                throw ApiException("Error deleting event: ${e.message}")
            }
        }
    }
}

class ApiException(message: String) : Exception(message)