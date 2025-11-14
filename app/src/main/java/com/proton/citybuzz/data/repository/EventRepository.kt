package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.EventDao

import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventAttendee

class EventRepository(private val dao: EventDao) {
    suspend fun addEvent(event: Event) = dao.insertEvent(event)
    suspend fun removeEvent(eventId: Long) = dao.deleteEvent(eventId)
    suspend fun getAllEvents() = dao.getAllEvents()
    suspend fun addAttendee(eventId: Long, userId: Long) =
        dao.insertAttendee(EventAttendee(eventId, userId))
    suspend fun removeAttendee(eventId: Long, userId: Long) =
        dao.deleteAttendee(eventId, userId)
    suspend fun getAttendees(eventId: Long) = dao.getAttendees(eventId)

    suspend fun getMyEvents(userId: Long) = dao.getMyEvents(userId)

    suspend fun getSuggestedEvents(userId: Long) = dao.getSuggestedEvents(userId)
}