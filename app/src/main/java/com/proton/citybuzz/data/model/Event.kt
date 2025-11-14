package com.proton.citybuzz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

enum class EventPrivacy {
    PUBLIC,
    FRIENDS_ONLY,
    FRIENDS_OF_FRIENDS
}

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: LocalDate,
    val time: LocalTime,
    val description: String,
    val location: String,
    val privacy: EventPrivacy,
    val idUser: Long
)
