package com.proton.citybuzz

import android.app.Application
import androidx.room.Room
import com.proton.citybuzz.data.local.AppDatabase
import com.proton.citybuzz.data.model.FriendRequest
import com.proton.citybuzz.data.repository.EventRepository
import com.proton.citybuzz.data.repository.FriendRequestRepository
import com.proton.citybuzz.data.repository.UserRepository
import com.proton.citybuzz.ui.viewmodel.EventViewModel
import com.proton.citybuzz.ui.viewmodel.SocialViewModel

class CityBuzzApp : Application() {

    companion object {
        lateinit var eventViewModel: EventViewModel
            private set

        lateinit var socialViewModel: SocialViewModel
            private set
    }

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "citybuzz_db"
        ).build()

        val eventRepo = EventRepository(db.eventDao())
        eventViewModel = EventViewModel(eventRepo)

        val userRepo = UserRepository(db.userDao())
        val requestRepo = FriendRequestRepository(db.friendRequestDao())
        socialViewModel = SocialViewModel(userRepo, requestRepo)
    }
}
