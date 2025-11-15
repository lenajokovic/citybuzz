package com.proton.citybuzz

import android.app.Application
import com.proton.citybuzz.data.repository.NotificationRepository
import com.proton.citybuzz.ui.viewmodel.EventViewModel
import com.proton.citybuzz.ui.viewmodel.NotificationViewModel
import com.proton.citybuzz.ui.viewmodel.SocialViewModel

class CityBuzzApp : Application() {
    var notificationRepository: NotificationRepository = NotificationRepository()
    var eventViewModel : EventViewModel = EventViewModel()
    var notificationViewModel : NotificationViewModel = NotificationViewModel(notificationRepository)
    var socialViewModel : SocialViewModel = SocialViewModel(notifRepo = notificationRepository)

    companion object {
        @Volatile
        private var instance: CityBuzzApp? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: CityBuzzApp().also { instance = it }
            }
    }

    override fun onCreate() {
        super.onCreate()
    }
}
