package com.proton.citybuzz.data.local

import java.sql.ResultSet
import java.time.LocalDate

import com.proton.citybuzz.snowflaketest.SnowflakeCaller
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventAttendee

class SfEventDao (private val sf: SnowflakeCaller = SnowflakeCaller.getInstance()) {

    // INSERT EVENT
    suspend fun insertEvent(event: Event) {
        val query = """
            INSERT INTO EVENTS (TITLE, DATE, DESCRIPTION, LOC, PRIVACY, USER_ID)
            VALUES (
                '${event.title}',
                '${event.date}', 
                '${event.description}',
                '${event.location}',
                ${event.privacy.ordinal},
                ${event.creatorId}
            )
        """
        sf.executeQuery(query)
    }

    // DELETE EVENT
    suspend fun deleteEvent(eventId: Long) {
        sf.executeQuery("DELETE FROM EVENTS WHERE EVENT_ID = $eventId")
    }

    /*
    @Insert
    suspend fun insertAttendee(attendee: EventAttendee)

    @Query("""
        DELETE FROM event_attendees
        WHERE eventId = :eventId AND userId = :userId
    """)
    suspend fun deleteAttendee(eventId: Long, userId: Long)

     */

    // GET EVENT ATTENDEES
    suspend fun getAttendees(eventId: Long): List<Long> {
        val rs = sf.executeQuery("SELECT USER_ID FROM EVENT_ATTENDEES WHERE EVENT_ID = $eventId")
        val list = mutableListOf<Long>()

        while (rs.next()) {
            list.add(rs.getLong("USER_ID"))
        }
        return list
    }

    // GET MY EVENTS
    suspend fun getMyEvents(userId: Long): List<Event> {
        val query = """
            SELECT * FROM EVENTS
            WHERE USER_ID = $userId
               OR EVENT_ID IN (
                    SELECT EVENT_ID 
                    FROM EVENT_ATTENDEES 
                    WHERE USER_ID = $userId
               )
        """
        return parseEventList(sf.executeQuery(query))
    }

    // GET SUGGESTED EVENTS
    suspend fun getSuggestedEvents(userId: Long): List<Event> {
        val query = """
            SELECT DISTINCT e.*
            FROM EVENTS e
            WHERE
                -- PUBLIC
                e.PRIVACY = 0
                OR
                (
                    e.PRIVACY = 1
                    AND e.USER_ID IN (
                        SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = $userId
                        UNION
                        SELECT USER_ID FROM USER_FRIENDS WHERE FRIEND_ID = $userId
                    )
                )
                OR
                (
                    e.PRIVACY = 2
                    AND e.EVENT_ID IN (
                        SELECT EVENT_ID
                        FROM EVENT_ATTENDEES
                        WHERE USER_ID IN (
                            SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = $userId
                            UNION
                            SELECT USER_ID FROM USER_FRIENDS WHERE FRIEND_ID = $userId
                        )
                    )
                )
        """
        return parseEventList(sf.executeQuery(query))
    }

    private fun parseEventList(rs: ResultSet): List<Event> {
        val list = mutableListOf<Event>()
        while (rs.next()) {
            list.add(
                Event(
                    id = rs.getLong("ID"),
                    title = rs.getString("TITLE"),
                    date = LocalDate.parse(rs.getString("DATE")),
                    //time = LocalTime.parse(rs.getString("TIME")),
                    description = rs.getString("DESCRIPTION"),
                    location = rs.getString("LOCATION"),
                    //privacy = EventPrivacy.valueOf(rs.getString("PRIVACY")),
                    creatorId = rs.getLong("CREATORID")
                )
            )
        }
        return list
    }
}