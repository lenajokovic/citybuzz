package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.FriendRequest
import com.proton.citybuzz.data.repository.UserRepository
import com.proton.citybuzz.data.repository.FriendRequestRepository

class SocialViewModel(
    private val userRepo: UserRepository,
    private val requestRepo: FriendRequestRepository
) : ViewModel() {

    val users = MutableLiveData<List<User>>()
    val loggedInUser = MutableLiveData<User?>()
    val friends = MutableLiveData<List<Int>>()
    val pendingRequests = MutableLiveData<List<FriendRequest>>()
    val suggestions = MutableLiveData<List<User>>()

    // User funkcije
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

    suspend fun getUser(userId: Int?): User? {
        return userRepo.getUser(userId)
    }

    // FriendRequest funkcije
    fun loadPendingRequests(userId: Int) = viewModelScope.launch {
        pendingRequests.value = requestRepo.getPendingRequests(userId)
    }

    fun sendRequest(fromUserId: Int, toUserId: Int) = viewModelScope.launch {
        requestRepo.sendRequest(fromUserId, toUserId)
        loadPendingRequests(toUserId)
    }

    fun acceptRequest(request: FriendRequest) = viewModelScope.launch {
        requestRepo.deleteRequest(request.userId, request.friendId)
        userRepo.addFriend(request.userId, request.friendId)
        loadPendingRequests(request.userId)
    }

    fun rejectRequest(request: FriendRequest) = viewModelScope.launch {
        requestRepo.deleteRequest(request.userId, request.friendId)
        loadPendingRequests(request.userId)
    }

    fun loadFriendSuggestions(userId: Int) = viewModelScope.launch {
        suggestions.value = userRepo.getFriendsOfFriends(userId)
    }
}