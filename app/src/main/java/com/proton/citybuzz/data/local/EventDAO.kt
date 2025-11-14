package com.proton.citybuzz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventAttendee

@Dao
interface EventDao {
    @Insert
    suspend fun insertEvent(event: Event)

    @Insert
    suspend fun insertAttendee(attendee: EventAttendee)

    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<Event>

    @Query("SELECT userId FROM event_attendees WHERE eventId = :eventId")
    suspend fun getAttendees(eventId: Long): List<Long>
}