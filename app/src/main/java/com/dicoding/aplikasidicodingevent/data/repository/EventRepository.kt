package com.dicoding.aplikasidicodingevent.data.repository

import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import com.dicoding.aplikasidicodingevent.data.local.EventEntity
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getActiveEvents(): Flow<List<ListEventsItem>>
    fun getFinishedEvents(): Flow<List<ListEventsItem>>
    fun searchEvents(query: String): Flow<List<ListEventsItem>>
    fun getFavoriteEvents(): Flow<List<EventEntity>>
    fun isEventFavorite(id: Int): Flow<Boolean>
    suspend fun insertEvent(event: EventEntity)
    suspend fun deleteEvent(event: EventEntity)
    fun getNearestActiveEvent(): Flow<ListEventsItem?>
}
