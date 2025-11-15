package com.proton.citybuzz.data.repository

import com.proton.citybuzz.data.local.SfUserDao

import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.UserFriend

class UserRepository(private val dao: SfUserDao = SfUserDao()) {
    suspend fun addUser(user: User) = dao.insertUser(user)

    suspend fun getUserByEmail(email: String): User? = dao.getUserByEmail(email)
    suspend fun getUserById(userId: Int?): User? = dao.getUserById(userId)
    suspend fun login(email: String, password: String): User? {
        val user = dao.getUserByEmail(email)
        return if (user?.password == password) user else null
    }

    suspend fun getAllUsers() = dao.getAllUsers()
    suspend fun addFriend(userId: Int, friendId: Int) =
        dao.insertFriend(UserFriend(userId, friendId))

    suspend fun removeFriend(userId: Int, friendId: Int) =
        dao.deleteFriend(userId, friendId)

    suspend fun getFriends(userId: Int) = dao.getFriends(userId)

    suspend fun getFriendsOfFriends(userId: Int): List<User> {

        // 1. Učitaj moje prijatelje (User objekti)
        val myFriends: List<User> = dao.getFriends(userId)

        // Set ID-eva mojih prijatelja radi brže provere
        val myFriendIds = myFriends.map { it.id.toInt() }.toSet()

        val suggestions = mutableSetOf<Int>()

        // 2. Za svakog mog prijatelja — uzmi njegove prijatelje
        for (friend in myFriends) {

            val friendsOfFriend: List<User> = dao.getFriends(friend.id.toInt())

            for (fof in friendsOfFriend) {

                val fofId = fof.id.toInt()

                // 3. Uslovi:
                // - da nisam ja
                // - da već nisu moji prijatelji
                if (fofId != userId && fofId !in myFriendIds) {
                    suggestions.add(fofId)
                }
            }
        }

        // 4. Ako nema predloga vrati prazno
        if (suggestions.isEmpty()) return emptyList()

        // 5. Uzmi kompletne User objekte
        return dao.getUsersByIds(suggestions.toList())
    }
}