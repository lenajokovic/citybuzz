package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.UserDao

import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.UserFriend

class UserRepository(private val dao: UserDao) {
    suspend fun addUser(user: User) = dao.insertUser(user)

    suspend fun getUserByEmail(email: String): User? = dao.getUserByEmail(email)
    suspend fun getUser(userId: Long?): User = dao.getUser(userId)
    suspend fun login(email: String, password: String): User? {
        val user = dao.getUserByEmail(email)
        return if (user?.password == password) user else null
    }

    suspend fun getAllUsers() = dao.getAllUsers()
    suspend fun addFriend(userId: Long, friendId: Long) =
        dao.insertFriend(UserFriend(userId, friendId))

    suspend fun removeFriend(userId: Long, friendId: Long) =
        dao.deleteFriend(userId, friendId)

    suspend fun getFriends(userId: Long) = dao.getFriends(userId)

    suspend fun getFriendsOfFriends(userId: Long): List<User> {
        val myFriends = dao.getFriends(userId).toSet()

        val suggestions = mutableSetOf<Long>()

        for (friendId in myFriends) {
            val friendsOfFriend = dao.getFriends(friendId)

            for (fof in friendsOfFriend) {
                if (fof != userId && fof !in myFriends) {
                    suggestions.add(fof)
                }
            }
        }

        return if (suggestions.isEmpty()) emptyList()
        else dao.getUsersByIds(suggestions.toList())
    }
}