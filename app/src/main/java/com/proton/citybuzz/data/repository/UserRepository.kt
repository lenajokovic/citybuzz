package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.UserDao

import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.UserFriend

class UserRepository(private val dao: UserDao) {
    suspend fun addUser(user: User) = dao.insertUser(user)
    suspend fun addFriend(userId: Long, friendId: Long) =
        dao.insertFriend(UserFriend(userId, friendId))
    suspend fun getFriends(userId: Long) = dao.getFriends(userId)
    suspend fun getAllUsers() = dao.getAllUsers()
}