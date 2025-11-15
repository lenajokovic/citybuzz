package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.SfEventDao

import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventAttendee

class EventRepository(private val dao: SfEventDao = SfEventDao()) {
    suspend fun addEvent(event: Event) = dao.insertEvent(event)
    suspend fun removeEvent(eventId: Int) = dao.deleteEvent(eventId)
    suspend fun getAllEvents() = dao.getAllEvents()
    suspend fun addAttendee(eventId: Int, userId: Int) =
        dao.insertAttendee(eventId, userId)
    suspend fun removeAttendee(eventId: Int, userId: Int) =
        dao.deleteAttendee(eventId, userId)
    suspend fun getAttendees(eventId: Int) = dao.getAttendees(eventId)

    suspend fun getMyEvents(userId: Int) = dao.getMyEvents(userId)

    suspend fun getSuggestedEvents(userId: Int) = dao.getSuggestedEvents(userId)

    suspend fun getMaxEventId() = dao.getMaxEventId()
}