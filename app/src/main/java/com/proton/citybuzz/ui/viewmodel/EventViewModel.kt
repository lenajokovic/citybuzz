package com.proton.citybuzz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.repository.EventRepository

class EventViewModel(private val eventRepo: EventRepository) : ViewModel() {
    val events = MutableLiveData<List<Event>>()
    val attendees = MutableLiveData<List<Long>>() // lista userId-a

    fun loadEvents() = viewModelScope.launch {
        events.value = eventRepo.getAllEvents()
    }

    fun addEvent(title: String, description: String, location: String) = viewModelScope.launch {
        val id = (eventRepo.getAllEvents().maxOfOrNull { it.id } ?: 0) + 1
        eventRepo.addEvent(Event(id, title, description, location))
        loadEvents()
    }

    fun addAttendee(eventId: Long, userId: Long) = viewModelScope.launch {
        eventRepo.addAttendee(eventId, userId)
        attendees.value = eventRepo.getAttendees(eventId)
    }
}