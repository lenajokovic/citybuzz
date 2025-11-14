package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventPrivacy
import com.proton.citybuzz.data.repository.EventRepository
import java.time.LocalDate
import java.time.LocalTime

class EventViewModel(private val eventRepo: EventRepository) : ViewModel() {

    val events = MutableLiveData<List<Event>>()
    val attendees = MutableLiveData<List<Long>>()
    val myEvents = MutableLiveData<List<Event>>()
    val suggestedEvents = MutableLiveData<List<Event>>()

    fun loadEvents() = viewModelScope.launch {
        events.value = eventRepo.getAllEvents()
    }

    fun addEvent(title: String, description: String, location: String, date: LocalDate,
        time: LocalTime, privacy: Int, idUser: Long
    ) = viewModelScope.launch {
        val event = Event(title = title, date = date, time = time, description = description,
            location = location, privacy = EventPrivacy.entries[privacy], creatorId = idUser
        )
        eventRepo.addEvent(event)
        loadEvents()
    }

    fun removeEvent(eventId: Long) = viewModelScope.launch {
        eventRepo.removeEvent(eventId)
        loadEvents()
    }

    fun addAttendee(eventId: Long, userId: Long) = viewModelScope.launch {
        eventRepo.addAttendee(eventId, userId)
        attendees.value = eventRepo.getAttendees(eventId)
    }

    fun removeAtendee(eventId: Long, userId: Long) = viewModelScope.launch {
        eventRepo.removeAttendee(eventId, userId)
        attendees.value = eventRepo.getAttendees(eventId)
    }

    fun loadMyEvents(userId: Long) = viewModelScope.launch {
        myEvents.value = eventRepo.getMyEvents(userId)
    }

    fun loadSuggestedEvents(userId: Long) = viewModelScope.launch {
        suggestedEvents.value = eventRepo.getSuggestedEvents(userId)
    }

    fun getTitle(eventId: Long): String? =
        events.value?.firstOrNull { it.id == eventId }?.title

    fun getDate(eventId: Long): LocalDate? =
        events.value?.firstOrNull { it.id == eventId }?.date

    fun getTime(eventId: Long): LocalTime? =
        events.value?.firstOrNull { it.id == eventId }?.time

    fun getDescription(eventId: Long): String? =
        events.value?.firstOrNull { it.id == eventId }?.description

    fun getLocation(eventId: Long): String? =
        events.value?.firstOrNull { it.id == eventId }?.location

    fun getPrivacy(eventId: Long): EventPrivacy? =
        events.value?.firstOrNull { it.id == eventId }?.privacy

    fun getCreatorId(eventId: Long): Long? =
        events.value?.firstOrNull { it.id == eventId }?.creatorId
}
