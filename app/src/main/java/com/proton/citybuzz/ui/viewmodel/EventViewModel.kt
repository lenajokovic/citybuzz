package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.proton.citybuzz.CityBuzzApp
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventPrivacy
import com.proton.citybuzz.data.model.Notification
import com.proton.citybuzz.data.model.NotificationType
import com.proton.citybuzz.data.repository.EventRepository
import com.proton.citybuzz.data.repository.NotificationRepository
import com.proton.citybuzz.data.repository.UserRepository
import java.time.LocalDateTime

class EventViewModel(
    private val eventRepo: EventRepository,
    private val notifRepo: NotificationRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    val events = MutableLiveData<List<Event>>()
    val attendees = MutableLiveData<List<Int>>()
    val myEvents = MutableLiveData<List<Event>>()
    val suggestedEvents = MutableLiveData<List<Event>>()

    fun loadEvents() = viewModelScope.launch {
        events.value = eventRepo.getAllEvents()
    }

    fun addEvent(title: String, description: String, location: String, date: LocalDateTime,
                 privacy: Int, idUser: Int
    ) = viewModelScope.launch {
        val event = Event(id = eventRepo.getMaxEventId() + 1, title = title, date = date, description = description,
            location = location, privacy = EventPrivacy.entries[privacy], creatorId = idUser
        )
        eventRepo.addEvent(event)
        loadEvents()
    }

    fun removeEvent(eventId: Int) = viewModelScope.launch {
        eventRepo.removeEvent(eventId)
        loadEvents()
    }

    fun addAttendee(eventId: Int, userId: Int) = viewModelScope.launch {
        eventRepo.addAttendee(eventId, userId)
        attendees.value = eventRepo.getAttendees(eventId)

        val event = eventRepo.getEventById(eventId)
        val user = userRepo.getUserById(userId)

        event?.let { e ->
            notifRepo.addNotification(
                userId = e.creatorId,
                type = NotificationType.EVENT_JOIN,
                message = "${user?.name ?: "A user"} joined your event: ${e.title}"
            )
        }

        loadMyEvents(userId)
        loadSuggestedEvents(userId)
    }

    fun removeAttendee(eventId: Int, userId: Int) = viewModelScope.launch {
        eventRepo.removeAttendee(eventId, userId)
        attendees.value = eventRepo.getAttendees(eventId)

        val event = eventRepo.getEventById(eventId)
        val user = userRepo.getUserById(userId)

        event?.let { e ->
            notifRepo.addNotification(
                userId = e.creatorId,
                type = NotificationType.EVENT_LEAVE,
                message = "${user?.name ?: "A user"} left your event: ${e.title}"
            )
        }

        loadMyEvents(userId)
        loadSuggestedEvents(userId)
    }

    fun sendEventInvite(eventId: Int, fromUserId: Int, toUserId: Int) = viewModelScope.launch {
        val event = eventRepo.getEventById(eventId)
        val sender = userRepo.getUserById(fromUserId)

        if (event == null || sender == null) return@launch

        notifRepo.addNotification(
            userId = toUserId,
            type = NotificationType.EVENT_INVITE,
            message = "${sender.name} invited you to the event: ${event.title}"
        )
    }

    fun loadMyEvents(userId: Int) = viewModelScope.launch {
        myEvents.value = eventRepo.getMyEvents(userId)
    }

    fun loadSuggestedEvents(userId: Int) = viewModelScope.launch {
        suggestedEvents.value = eventRepo.getSuggestedEvents(userId)
    }

    fun getTitle(eventId: Int): String? =
        events.value?.firstOrNull { it.id == eventId }?.title

    fun getDate(eventId: Int): LocalDateTime? =
        events.value?.firstOrNull { it.id == eventId }?.date

    fun getDescription(eventId: Int): String? =
        events.value?.firstOrNull { it.id == eventId }?.description

    fun getLocation(eventId: Int): String? =
        events.value?.firstOrNull { it.id == eventId }?.location

    fun getPrivacy(eventId: Int): EventPrivacy? =
        events.value?.firstOrNull { it.id == eventId }?.privacy

    fun getCreatorId(eventId: Int): Int? =
        events.value?.firstOrNull { it.id == eventId }?.creatorId
}
