package com.proton.citybuzz.data.model

import androidx.room.Entity

@Entity(
    tableName = "event_attendees",
    primaryKeys = ["eventId", "userId"]
)
data class EventAttendee(
    val eventId: Int,
    val userId: Int
)
