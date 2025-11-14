package com.proton.citybuzz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.UserFriend

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    suspend fun getUsersByIds(ids: List<Long>): List<User>

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Insert
    suspend fun insertFriend(friend: UserFriend)

    @Query(
        "DELETE FROM user_friends " +
                "WHERE (userId = :userId AND friendId = :friendId) " +
                "   OR (userId = :friendId AND friendId = :userId)"
    )
    suspend fun deleteFriend(userId: Long, friendId: Long)

    @Query(
        "SELECT friendId FROM user_friends WHERE userId = :userId " +
                "UNION " +
                "SELECT userId FROM user_friends WHERE friendId = :userId"
    )
    suspend fun getFriends(userId: Long): List<Long>
}