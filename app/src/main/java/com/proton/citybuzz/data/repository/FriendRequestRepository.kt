package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.FriendRequestDao
import com.proton.citybuzz.data.model.FriendRequest

class FriendRequestRepository(private val dao: FriendRequestDao) {

    suspend fun sendRequest(fromUserId: Long, toUserId: Long) {
        dao.insertRequest(FriendRequest(fromUserId = fromUserId, toUserId = toUserId))
    }

    suspend fun getPendingRequests(userId: Long): List<FriendRequest> =
        dao.getPendingRequests(userId)

    suspend fun acceptRequest(request: FriendRequest) {
        dao.updateRequest(request.copy(status = "accepted"))
    }

    suspend fun rejectRequest(request: FriendRequest) {
        dao.updateRequest(request.copy(status = "rejected"))
    }

    suspend fun deleteRequest(requestId: Long) = dao.deleteRequest(requestId)
}