package com.proton.citybuzz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_requests")
data class FriendRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromUserId: Long,
    val toUserId: Long,
    val status: String = "pending" // "pending", "accepted", "rejected"
)
