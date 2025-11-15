package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.SfFriendRequestDao
import com.proton.citybuzz.data.model.FriendRequest

class FriendRequestRepository(private val dao: SfFriendRequestDao) {

    suspend fun sendRequest(fromUserId: Int, toUserId: Int) {
        dao.insertRequest(FriendRequest(userId = fromUserId, friendId = toUserId))
    }

    suspend fun getPendingRequests(userId: Int): List<FriendRequest> =
        dao.getPendingRequests(userId)

    suspend fun deleteRequest(fromUserId: Int, toUserId: Int) {
        dao.deleteRequest(fromUserId, toUserId)
    }

}