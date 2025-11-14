package com.proton.citybuzz.data.model

import androidx.room.Entity

@Entity(
    tableName = "user_friends",
    primaryKeys = ["userId", "friendId"]
)
data class UserFriend(
    val userId: Long,
    val friendId: Long
)
