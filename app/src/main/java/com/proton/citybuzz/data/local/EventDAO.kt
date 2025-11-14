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

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEvent(eventId: Long)
    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<Event>
    @Insert
    suspend fun insertAttendee(attendee: EventAttendee)

    @Query("""
        DELETE FROM event_attendees
        WHERE eventId = :eventId AND userId = :userId
    """)
    suspend fun deleteAttendee(eventId: Long, userId: Long)

    @Query("SELECT userId FROM event_attendees WHERE eventId = :eventId")
    suspend fun getAttendees(eventId: Long): List<Long>

    @Query("""
        SELECT * FROM events 
        WHERE creatorId = :userId 
           OR id IN (SELECT eventId FROM event_attendees WHERE userId = :userId)
    """)
    suspend fun getMyEvents(userId: Long): List<Event>

    @Query("""
        SELECT DISTINCT e.* 
        FROM events e
        WHERE
            -- PUBLIC
            e.privacy = 'PUBLIC'
            OR
            -- FRIENDS_ONLY
            (   
                e.privacy = 'FRIENDS_ONLY'
                AND e.creatorId IN (
                    SELECT friendId FROM user_friends WHERE userId = :userId
                    UNION
                    SELECT userId FROM user_friends WHERE friendId = :userId
                )
            )
            OR
            -- 3. FRIENDS_OF_FRIENDS
            (
                e.privacy = 'FRIENDS_OF_FRIENDS'
                AND e.id IN (
                    SELECT eventId 
                    FROM event_attendees
                    WHERE userId IN (
                        SELECT friendId FROM user_friends WHERE userId = :userId
                        UNION
                        SELECT userId FROM user_friends WHERE friendId = :userId
                    )
                )
            )
    """)
    suspend fun getSuggestedEvents(userId: Long): List<Event>
}