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

    @Insert
    suspend fun insertFriend(friend: UserFriend)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT friendId FROM user_friends WHERE userId = :userId")
    suspend fun getFriends(userId: Long): List<Long>
}