package com.proton.citybuzz.data.local

import com.proton.citybuzz.snowflaketest.SnowflakeCaller
import com.proton.citybuzz.data.model.FriendRequest

class SfFriendRequestDao (private val sf: SnowflakeCaller = SnowflakeCaller.getInstance()) {
    // INSERT FRIEND REQUEST
    suspend fun insertRequest(request: FriendRequest) {
        val query = """
            INSERT INTO FRIEND_REQUESTS (USER_ID, FRIEND_ID)
            VALUES (${request.userId}, ${request.friendId})
        """.trimIndent()

        sf.executeUpdate(query)
    }

    suspend fun getPendingRequests(userId: Int): List<FriendRequest> {
        val query = """
        SELECT USER_ID, FRIEND_ID
        FROM FRIEND_REQUESTS
        WHERE FRIEND_ID = $userId
    """.trimIndent()

        val rs = sf.executeQuery(query)
        val list = mutableListOf<FriendRequest>()

        while (rs.next()) {
            list.add(
                FriendRequest(
                    userId = rs.getInt("USER_ID"),
                    friendId = rs.getInt("FRIEND_ID")
                )
            )
        }

        return list
    }

    // DELETE FRIEND REQUEST (userId -> friendId)
    suspend fun deleteRequest(userId: Int, friendId: Int) {
        val query = """
            DELETE FROM FRIEND_REQUESTS
            WHERE USER_ID = $userId
              AND FRIEND_ID = $friendId
        """.trimIndent()

        sf.executeUpdate(query)
    }
}