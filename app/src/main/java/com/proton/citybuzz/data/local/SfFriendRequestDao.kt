package com.proton.citybuzz.data.local

import com.proton.citybuzz.snowflaketest.SnowflakeCaller
import com.proton.citybuzz.data.model.FriendRequest

class SfFriendRequestDao (private val sf: SnowflakeCaller = SnowflakeCaller.getInstance()) {
    // INSERT FRIEND REQUEST
    suspend fun insertRequest(request: FriendRequest) {
        val query = """
            INSERT INTO FRIEND_REQUESTS (USER_ID, FRIEND_ID)
            VALUES (${request.fromUserId}, ${request.toUserId})
        """.trimIndent()

        sf.executeUpdate(query)
    }

    suspend fun getPendingRequests(toUserId: Int): List<FriendRequest> {
        val query = """
        SELECT USER_ID, FRIEND_ID
        FROM FRIEND_REQUESTS
        WHERE FRIEND_ID = $toUserId
    """.trimIndent()

        val rs = sf.executeQuery(query)
        val list = mutableListOf<FriendRequest>()

        while (rs.next()) {
            list.add(
                FriendRequest(
                    fromUserId = rs.getInt("USER_ID"),
                    toUserId = rs.getInt("FRIEND_ID")
                )
            )
        }

        return list
    }

    // DELETE FRIEND REQUEST (userId -> friendId)
    suspend fun deleteRequest(fromUserId: Int, toUserId: Int) {
        val query = """
            DELETE FROM FRIEND_REQUESTS
            WHERE USER_ID = $fromUserId
              AND FRIEND_ID = $toUserId
        """.trimIndent()

        sf.executeUpdate(query)
    }
}