package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventPrivacy
import com.proton.citybuzz.data.repository.EventRepository
import java.time.LocalDateTime

class EventViewModel(private val eventRepo: EventRepository = EventRepository()) : ViewModel() {

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
        val list = eventRepo.getAttendees(eventId)
        attendees.value = list
    }

    fun removeAttendee(eventId: Int, userId: Int) = viewModelScope.launch {
        eventRepo.removeAttendee(eventId, userId)
        attendees.value = eventRepo.getAttendees(eventId)
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
