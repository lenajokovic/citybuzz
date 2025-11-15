package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.SfNotificationDao

import com.proton.citybuzz.data.model.Notification
import com.proton.citybuzz.data.model.NotificationType

class NotificationRepository(private val dao: SfNotificationDao = SfNotificationDao()) {

    suspend fun addNotification(userId: Int, type: NotificationType, message: String) =
        dao.addNotification(userId, type, message)

    suspend fun getForUser(userId: Int) =
        dao.getNotifications(userId)

    suspend fun markRead(id: Int) =
        dao.markAsRead(id)
}