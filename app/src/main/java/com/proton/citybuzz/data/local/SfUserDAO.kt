package com.proton.citybuzz.data.local

import java.sql.ResultSet

import com.proton.citybuzz.SnowflakeCaller
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.UserFriend

class SfUserDao (private val sf: SnowflakeCaller = SnowflakeCaller.getInstance()) {
    // INSERT USER
    suspend fun insertUser(user: User) {
        val query = """
            INSERT INTO USERS (USER_ID, NAME, EMAIL, PASSWORD, PROFILEIMAGE)
            VALUES (
                ${user.id},
                '${user.name}',
                '${user.email}',
                '${user.password}',
                ${if (user.profileImage == null) "NULL" else "'${user.profileImage}'"}
            )
        """.trimIndent()

        sf.executeUpdate(query)
    }

    // GET ALL USERS
    suspend fun getAllUsers(): List<User> {
        val rs = sf.executeQuery("SELECT * FROM USERS")
        return parseUsers(rs)
    }

    // GET USER BY ID
    suspend fun getUser(userId: Int?): User? {
        if (userId == null) return null
        val rs = sf.executeQuery("SELECT * FROM USERS WHERE USER_ID = $userId")
        return if (rs.next()) parseUser(rs) else null
    }

    // GET USERS BY IDS
    suspend fun getUsersByIds(ids: List<Int>): List<User> {
        if (ids.isEmpty()) return emptyList()

        val idList = ids.joinToString(",")
        val rs = sf.executeQuery("SELECT * FROM USERS WHERE USER_ID IN ($idList)")
        return parseUsers(rs)
    }

    // GET USER BY EMAIL
    suspend fun getUserByEmail(email: String): User? {
        val rs = sf.executeQuery("SELECT * FROM USERS WHERE EMAIL = '$email'")
        return if (rs.next()) parseUser(rs) else null
    }

    // INSERT FRIEND RELATION
    suspend fun insertFriend(friend: UserFriend) {
        val query = """
            INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID)
            VALUES (${friend.userId}, ${friend.friendId})
        """.trimIndent()

        sf.executeUpdate(query)
    }

    // DELETE FRIEND RELATION
    suspend fun deleteFriend(userId: Int, friendId: Int) {
        val query = """
            DELETE FROM USER_FRIENDS
            WHERE (USER_ID = $userId AND FRIEND_ID = $friendId)
               OR (USER_ID = $friendId AND FRIEND_ID = $userId)
        """.trimIndent()

        sf.executeQuery(query)
    }

    // GET FRIENDS
    suspend fun getFriends(userId: Int): List<User> {
        // 1. Uzmemo samo ID-eve prijatelja
        val idQuery = """
        SELECT FRIEND_ID AS ID FROM USER_FRIENDS WHERE USER_ID = $userId
        UNION
        SELECT USER_ID AS ID FROM USER_FRIENDS WHERE FRIEND_ID = $userId
    """.trimIndent()

        val rs = sf.executeQuery(idQuery)
        val friendIds = mutableListOf<Int>()

        while (rs.next()) {
            friendIds.add(rs.getInt("ID"))
        }

        if (friendIds.isEmpty()) return emptyList()

        // 2. Učitamo sve korisnike prema ID-jevima
        val usersQuery = """
        SELECT USER_ID, NAME, EMAIL, PASSWORD, PROFILEIMAGE
        FROM USERS
        WHERE USER_ID IN (${friendIds.joinToString(",")})
    """.trimIndent()

        val userRs = sf.executeQuery(usersQuery)
        val users = mutableListOf<User>()

        while (userRs.next()) {
            users.add(
                User(
                    id = userRs.getInt("USER_ID").toInt(),
                    name = userRs.getString("NAME"),
                    email = userRs.getString("EMAIL"),
                    password = userRs.getString("PASSWORD"),
                    profileImage = userRs.getString("PROFILEIMAGE")
                )
            )
        }

        return users
    }


    // ---------- PARSER FUNKCIJE ----------
    private fun parseUsers(rs: ResultSet): List<User> {
        val list = mutableListOf<User>()
        while (rs.next()) list.add(parseUser(rs))
        return list
    }

    private fun parseUser(rs: ResultSet): User {
        return User(
            id = rs.getInt("USER_ID"),
            name = rs.getString("NAME"),
            email = rs.getString("EMAIL"),
            password = rs.getString("PASSWORD"),
            profileImage = rs.getString("PROFILEIMAGE")
        )
    }
}