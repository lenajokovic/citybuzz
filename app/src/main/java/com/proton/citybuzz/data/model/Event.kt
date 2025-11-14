package com.proton.citybuzz.data.model

data class Event(
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val attendees: MutableList<Long> = mutableListOf() // lista ID korisnika koji idu
)
