package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.SfFriendRequestDao
import com.proton.citybuzz.data.model.FriendRequest

class FriendRequestRepository(private val dao: SfFriendRequestDao = SfFriendRequestDao()) {

    suspend fun sendRequest(fromUserId: Int, toUserId: Int) {
        dao.insertRequest(FriendRequest(fromUserId = fromUserId, toUserId = toUserId))
    }

    suspend fun getPendingRequests(toUserId: Int): List<FriendRequest> =
        dao.getPendingRequests(toUserId)

    suspend fun deleteRequest(fromUserId: Int, toUserId: Int) {
        dao.deleteRequest(fromUserId, toUserId)
    }

}