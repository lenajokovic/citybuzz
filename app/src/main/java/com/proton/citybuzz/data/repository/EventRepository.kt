package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventAttendee

class EventRepository() {
    suspend fun addEvent(event: Event) {}
    suspend fun removeEvent(eventId: Long) {}
    suspend fun getAllEvents() {}
    suspend fun addAttendee(eventId: Long, userId: Long) {}
    suspend fun removeAttendee(eventId: Long, userId: Long) {}
    suspend fun getAttendees(eventId: Long) {}

    suspend fun getMyEvents(userId: Long) {}

    suspend fun getSuggestedEvents(userId: Long) {}
}