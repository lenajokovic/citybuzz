package com.proton.citybuzz.data.local

import com.proton.citybuzz.SnowflakeCaller
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventPrivacy
import com.proton.citybuzz.data.model.User
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SfEventDao (private val sf: SnowflakeCaller = SnowflakeCaller.getInstance()) {

    // INSERT EVENT
    suspend fun insertEvent(event: Event) {
        val query = """
            INSERT INTO EVENTS (EVENT_ID, TITLE, DATE, DESCRIPTION, LOC, PRIVACY, USER_ID)
            VALUES ( 
                '${event.id}',
                '${event.title}',
                '${event.date}',
                '${event.description}',
                '${event.location}',
                ${event.privacy.ordinal},
                ${event.creatorId}
            )
        """
        sf.executeUpdate(query)
    }

    // DELETE EVENT
    suspend fun deleteEvent(eventId: Int) {
        sf.executeUpdate("DELETE FROM EVENTS WHERE EVENT_ID = $eventId")
        sf.executeUpdate("DELETE FROM EVENT_ATTENDEES WHERE EVENT_ID = $eventId")
    }

    //GET ALL EVENTS
    suspend fun getAllEvents(): List<Event> {
        val rs = sf.executeQuery("SELECT * FROM EVENTS ORDER BY DATE")
        return parseEvents(rs)
    }

    // GET EVENT BY ID
    suspend fun getEventById(eventId: Int?): Event? {
        if (eventId == null) return null
        val rs = sf.executeQuery("SELECT * FROM EVENTS WHERE EVENT_ID = $eventId")
        return if (rs.next()) parseEvent(rs) else null
    }

    // INSERT ATTENDEE
    suspend fun insertAttendee(eventId: Int, userId: Int) {
        val query = """
        INSERT INTO EVENT_ATTENDEES (EVENT_ID, USER_ID)
        VALUES ($eventId, $userId)
    """
        sf.executeUpdate(query)
    }

    // REMOVE ATTENDEE
    suspend fun deleteAttendee(eventId: Int, userId: Int) {
        val query = """
        DELETE FROM EVENT_ATTENDEES
        WHERE EVENT_ID = $eventId
          AND USER_ID = $userId
    """
        sf.executeUpdate(query)
    }

    // GET EVENT ATTENDEES
    suspend fun getAttendees(eventId: Int): List<Int> {
        val rs = sf.executeQuery("SELECT USER_ID FROM EVENT_ATTENDEES WHERE EVENT_ID = $eventId")
        val list = mutableListOf<Int>()

        while (rs.next()) {
            list.add(rs.getInt("USER_ID"))
        }
        return list
    }

    // GET MY EVENTS
    suspend fun getMyEvents(userId: Int): List<Event> {
        val query = """
            SELECT * FROM EVENTS
            WHERE USER_ID = $userId
               OR EVENT_ID IN (
                    SELECT EVENT_ID 
                    FROM EVENT_ATTENDEES 
                    WHERE USER_ID = $userId
               )
            ORDER BY DATE
        """
        return parseEvents(sf.executeQuery(query))
    }

    // GET SUGGESTED EVENTS
    suspend fun getSuggestedEvents(userId: Int): List<Event> {
        val query = """
            SELECT DISTINCT e.*
            FROM EVENTS e
            WHERE
                e.EVENT_ID NOT IN (
                    SELECT EVENT_ID FROM EVENT_ATTENDEES WHERE USER_ID = $userId
                )
                
                AND e.USER_ID != $userId
                
                AND
                (
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
                )
        """
        return parseEvents(sf.executeQuery(query))
    }

    // ---------- PARSER FUNKCIJE ----------
    private fun parseEvents(rs: ResultSet): List<Event> {
        val list = mutableListOf<Event>()
        while (rs.next()) list.add(parseEvent(rs))
        return list
    }

    private fun parseEvent(rs: ResultSet): Event {
        val formatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        return Event(
            id = rs.getInt("EVENT_ID"),
            title = rs.getString("TITLE"),
            date = LocalDateTime.parse(rs.getString("DATE"), formatter),
            description = rs.getString("DESCRIPTION"),
            location = rs.getString("LOC"),
            privacy = EventPrivacy.entries[rs.getInt("PRIVACY")],
            creatorId = rs.getInt("USER_ID")
        )
    }

    suspend fun getMaxEventId(): Int {
        val rs = sf.executeQuery("SELECT MAX(EVENT_ID) AS maxId FROM EVENTS")
        var maxId = 0
        if (rs.next()) {
            maxId = rs.getInt("MAXID")
        }
        return maxId
    }
}