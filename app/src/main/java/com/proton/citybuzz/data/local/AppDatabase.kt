package com.proton.citybuzz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.UserFriend
import com.proton.citybuzz.data.model.EventAttendee
import com.proton.citybuzz.data.model.EventConverters
import com.proton.citybuzz.data.model.FriendRequest

@Database(
    entities = [User::class, Event::class, UserFriend::class, EventAttendee::class, FriendRequest::class],
    version = 1
)
@TypeConverters(EventConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun friendRequestDao(): FriendRequestDao
    abstract fun eventDao(): EventDao
}

/*

u mainactivity inicijalizacija?

val db = Room.databaseBuilder(
    applicationContext,
    AppDatabase::class.java,
    "citybuzz_db"
).build()

 */