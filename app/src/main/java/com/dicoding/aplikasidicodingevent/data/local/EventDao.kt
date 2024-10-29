package com.dicoding.aplikasidicodingevent.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Query("SELECT * FROM favorite_events")
    fun getFavoriteEvents(): Flow<List<EventEntity>>

    @Query("SELECT EXISTS(SELECT * FROM favorite_events WHERE id = :id)")
    fun isEventFavorite(id: Int): Flow<Boolean>
}
