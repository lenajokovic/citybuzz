package com.proton.citybuzz.data.model

enum class NotificationType {
    FRIEND_REQUEST,
    FRIEND_ACCEPTED,
    SUGGESTED_EVENT,
    EVENT_JOIN,
    EVENT_REMINDER
}

class Notification(
    val id: Int,
    val userId: Int,
    val type: NotificationType,
    val message: String,
    val createdAt: String,
    val isRead: Boolean
)