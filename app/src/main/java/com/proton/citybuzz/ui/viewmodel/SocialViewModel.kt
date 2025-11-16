package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.FriendRequest
import com.proton.citybuzz.data.model.Notification
import com.proton.citybuzz.data.model.NotificationType
import com.proton.citybuzz.data.repository.UserRepository
import com.proton.citybuzz.data.repository.FriendRequestRepository
import com.proton.citybuzz.data.repository.NotificationRepository

class SocialViewModel(
    private val userRepo: UserRepository = UserRepository(),
    private val requestRepo: FriendRequestRepository = FriendRequestRepository(),
    private val notifRepo : NotificationRepository = NotificationRepository()
) : ViewModel() {

    val users = MutableLiveData<List<User>>()
    var loggedInUser = MutableLiveData<User?>()
    val friends = MutableLiveData<List<User>>()
    val pendingRequests = MutableLiveData<List<FriendRequest>>()

    val outgoingRequests = MutableLiveData<Set<Int>>(emptySet())

    val suggestions = MutableLiveData<List<User>>()

    fun loadUsers() = viewModelScope.launch { users.value = userRepo.getAllUsers() }

    fun addUser(name: String, email: String, password: String, profileImage: String?) = viewModelScope.launch {
        val id = (userRepo.getAllUsers().maxOfOrNull { it.id } ?: 0) + 1
        userRepo.addUser(User(id, name, email, password, profileImage))
        loadUsers()
    }

    fun login(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val result = userRepo.login(email, password)
            loggedInUser.value = result
            onResult(result)
        }
    }

    fun loadFriends(userId: Int) = viewModelScope.launch {
        friends.value = userRepo.getFriends(userId)
    }

    fun removeFriend(userId: Int, friendId : Int) = viewModelScope.launch {
        userRepo.removeFriend(userId, friendId)
        loadFriends(userId)
    }

    suspend fun getUserById(userId: Int?): User? {
        return userRepo.getUserById(userId)
    }

    fun updateName(userId: Int, name: String) = viewModelScope.launch {
        userRepo.updateName(userId, name)
        loggedInUser.value = userRepo.getUserById(userId)
    }

    fun updateEmail(userId: Int, email: String) = viewModelScope.launch {
        userRepo.updateEmail(userId, email)
        loggedInUser.value = userRepo.getUserById(userId)
    }

    fun updatePassword(userId: Int, password: String) = viewModelScope.launch {
        userRepo.updatePassword(userId, password)
        loggedInUser.value = userRepo.getUserById(userId)
    }

    fun updateProfileImage(userId: Int, profileImage: String?) = viewModelScope.launch {
        userRepo.updateProfileImage(userId, profileImage)
        loggedInUser.value = userRepo.getUserById(userId)
    }

    // FriendRequest funkcije
    fun loadPendingRequests(toUserId: Int) = viewModelScope.launch {
        pendingRequests.value = requestRepo.getPendingRequests(toUserId)
    }

    fun sendRequest(fromUserId: Int, toUserId: Int) = viewModelScope.launch {
        requestRepo.sendRequest(fromUserId, toUserId)

        val sender = userRepo.getUserById(fromUserId)
        notifRepo.addNotification(
            userId = toUserId,
            type = NotificationType.FRIEND_REQUEST,
            message = "You received a friend request from ${sender?.name ?: "Unknown"}"
        )

        val current = outgoingRequests.value?.toMutableSet() ?: mutableSetOf()
        // Add a temporary FriendRequest object for UI
        current.add(toUserId)
        outgoingRequests.value = current
    }

    fun acceptRequest(request: FriendRequest) = viewModelScope.launch {
        requestRepo.deleteRequest(request.fromUserId, request.toUserId)
        userRepo.addFriend(request.fromUserId, request.toUserId)
        loadFriends(request.toUserId)

        val sender = userRepo.getUserById(request.fromUserId)
        notifRepo.addNotification(
            userId = request.toUserId,
            type = NotificationType.FRIEND_ACCEPTED,
            message = "You accepted a friend request from ${sender?.name ?: "Unknown"}"
        )

        loadPendingRequests(request.toUserId)
    }

    fun rejectRequest(request: FriendRequest) = viewModelScope.launch {
        requestRepo.deleteRequest(request.fromUserId, request.toUserId)
        loadPendingRequests(request.toUserId)
    }

    fun loadFriendSuggestions(userId: Int) = viewModelScope.launch {
        suggestions.value = userRepo.getFriendsOfFriends(userId)
    }

    suspend fun searchUsers(query: String): List<User> {
        return userRepo.searchUsersByName(query)
    }
}