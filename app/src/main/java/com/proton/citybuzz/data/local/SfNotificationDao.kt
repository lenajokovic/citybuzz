package com.proton.citybuzz.data.local

import com.proton.citybuzz.SnowflakeCaller
import com.proton.citybuzz.data.model.EventPrivacy
import com.proton.citybuzz.data.model.Notification
import com.proton.citybuzz.data.model.NotificationType

class SfNotificationDao(private val sf: SnowflakeCaller = SnowflakeCaller.getInstance()) {

    suspend fun addNotification(userId: Int, type: NotificationType, message: String) {
        val query = """
            INSERT INTO NOTIFICATIONS (USER_ID, TYPE, MESSAGE, CREATED_AT, IS_READ)
            VALUES ($userId, '${type.ordinal}', '$message', CURRENT_TIMESTAMP(), FALSE)
        """.trimIndent()

        sf.executeUpdate(query)
    }

    suspend fun getNotifications(userId: Int): List<Notification> {
        val query = """
            SELECT * FROM NOTIFICATIONS
            WHERE USER_ID = $userId
            ORDER BY CREATED_AT DESC
        """.trimIndent()

        val rs = sf.executeQuery(query)
        val list = mutableListOf<Notification>()

        while (rs.next()) {
            list.add(
                Notification(
                    id = rs.getInt("NOT_ID"),
                    userId = rs.getInt("USER_ID"),
                    type = NotificationType.entries[rs.getInt("TYPE")],
                    message = rs.getString("MESSAGE"),
                    createdAt = rs.getString("CREATED_AT"),
                    isRead = rs.getBoolean("IS_READ")
                )
            )
        }
        return list
    }

    suspend fun markAsRead(id: Int) {
        val query = "UPDATE NOTIFICATIONS SET IS_READ = TRUE WHERE NOT_ID = $id"
        sf.executeUpdate(query)
    }

    suspend fun removeNotification(id: Int) {
        val query = "DELETE FROM NOTIFICATIONS WHERE NOT_ID = $id"
        sf.executeUpdate(query)
    }
}