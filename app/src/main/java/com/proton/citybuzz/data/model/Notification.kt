package com.proton.citybuzz.data.model

// NOTIFICATIONS (NOT_ID int, USER_ID int, TYPE int, MESSAGE varchar, CREATED_AT varchar, IS_READ boolean)

enum class NotificationType {
    FRIEND_REQUEST,
    FRIEND_ACCEPTED,
    EVENT_JOIN,
    EVENT_LEAVE,
    EVENT_INVITE
}

class Notification(
    val id: Int,
    val userId: Int,
    val type: NotificationType,
    val message: String,
    val createdAt: String,
    val isRead: Boolean
)