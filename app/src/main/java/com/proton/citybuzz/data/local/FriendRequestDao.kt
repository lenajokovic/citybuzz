package com.proton.citybuzz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.proton.citybuzz.data.model.FriendRequest

@Dao
interface FriendRequestDao {
    @Insert
    suspend fun insertRequest(request: FriendRequest)

    @Query("SELECT * FROM friend_requests WHERE toUserId = :userId AND status = 'pending'")
    suspend fun getPendingRequests(userId: Long): List<FriendRequest>

    @Update
    suspend fun updateRequest(request: FriendRequest)

    @Query("DELETE FROM friend_requests WHERE id = :requestId")
    suspend fun deleteRequest(requestId: Long)
}