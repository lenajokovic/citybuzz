package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.Notification
import com.proton.citybuzz.data.model.NotificationType
import com.proton.citybuzz.data.repository.NotificationRepository

class NotificationViewModel(
    private val notifRepo: NotificationRepository = NotificationRepository()
) : ViewModel() {

    val notifications = MutableLiveData<List<Notification>>()

    fun loadNotifications(userId: Int) = viewModelScope.launch {
        notifications.value = notifRepo.getForUser(userId)
    }

    fun markRead(id: Int) = viewModelScope.launch {
        notifRepo.markRead(id)
    }
}