package com.proton.citybuzz.data.model

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class EventConverters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? = time?.toString()

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? = value?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromEventPrivacy(privacy: EventPrivacy): String = privacy.name

    @TypeConverter
    fun toEventPrivacy(value: String): EventPrivacy = EventPrivacy.valueOf(value)
}