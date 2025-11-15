package com.proton.citybuzz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

// EVENTS (EVENT_ID int, TITLE varchar, DATETIME date, DESCRIPTION varchar, LOC varchar, PRIVACY INT(0-2), USER_ID int

enum class EventPrivacy {
    PUBLIC,
    FRIENDS_ONLY,
    FRIENDS_OF_FRIENDS
}

class Event(
    val id: Int = 0,
    val creatorId: Int = 0,
    val title: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val description: String = "",
    val location: String = "",
    val privacy: EventPrivacy = EventPrivacy.PUBLIC,
)
