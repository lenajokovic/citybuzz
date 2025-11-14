package com.proton.citybuzz

import android.app.Application
import androidx.room.Room
import com.proton.citybuzz.data.local.AppDatabase

class CityBuzzApp : Application() {

    companion object {
        lateinit var db: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "citybuzz_db"
        ).build()
    }
}
