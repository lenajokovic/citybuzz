package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.repository.UserRepository

class UserViewModel(private val userRepo: UserRepository) : ViewModel() {
    val users = MutableLiveData<List<User>>()
    val friends = MutableLiveData<List<Long>>() // lista friendId-a za odabranog korisnika

    fun loadUsers() = viewModelScope.launch {
        users.value = userRepo.getAllUsers()
    }

    fun addUser(name: String, email: String) = viewModelScope.launch {
        val id = (userRepo.getAllUsers().maxOfOrNull { it.id } ?: 0) + 1
        userRepo.addUser(User(id, name, email))
        loadUsers()
    }

    fun addFriend(userId: Long, friendId: Long) = viewModelScope.launch {
        userRepo.addFriend(userId, friendId)
        friends.value = userRepo.getFriends(userId)
    }
}