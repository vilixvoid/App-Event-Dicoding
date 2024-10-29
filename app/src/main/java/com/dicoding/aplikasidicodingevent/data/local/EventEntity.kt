package com.dicoding.aplikasidicodingevent.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dicoding.aplikasidicodingevent.model.ListEventsItem
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite_events")
data class EventEntity(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val ownerName: String?,
    val beginTime: String?,
    val imageLogo: String?,
    val mediaCover: String?,
    val registrants: Int?,
    val link: String?,
    val description: String?,
    val quota: Int?
) : Parcelable {
    fun toListEventsItem(): ListEventsItem {
        return ListEventsItem(
            id = id,
            name = name,
            ownerName = ownerName,
            beginTime = beginTime,
            imageLogo = imageLogo,
            mediaCover = mediaCover,
            registrants = registrants,
            link = link,
            description = description,
            quota = quota
        )
    }
}