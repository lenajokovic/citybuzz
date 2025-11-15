package com.proton.citybuzz.data.local

import com.proton.citybuzz.SnowflakeCaller
import com.proton.citybuzz.data.model.FriendRequest

class SfFriendRequestDao (private val sf: SnowflakeCaller = SnowflakeCaller.getInstance()) {
    // INSERT FRIEND REQUEST
    suspend fun insertRequest(request: FriendRequest) {
        val query = """
            INSERT INTO FRIEND_REQUESTS (FROM_USER_ID, TO_USER_ID)
            VALUES (${request.fromUserId}, ${request.toUserId})
        """.trimIndent()

        sf.executeUpdate(query)
    }

    suspend fun getPendingRequests(toUserId: Int): List<FriendRequest> {
        val query = """
        SELECT FROM_USER_ID, TO_USER_ID
        FROM FRIEND_REQUESTS
        WHERE TO_USER_ID = $toUserId
    """.trimIndent()

        val rs = sf.executeQuery(query)
        val list = mutableListOf<FriendRequest>()

        while (rs.next()) {
            list.add(
                FriendRequest(
                    fromUserId = rs.getInt("FROM_USER_ID"),
                    toUserId = rs.getInt("TO_USER_ID")
                )
            )
        }

        return list
    }

    // DELETE FRIEND REQUEST (userId -> friendId)
    suspend fun deleteRequest(fromUserId: Int, toUserId: Int) {
        val query = """
            DELETE FROM FRIEND_REQUESTS
            WHERE FROM_USER_ID = $fromUserId
              AND TO_USER_ID = $toUserId
        """.trimIndent()

        sf.executeUpdate(query)
    }
}