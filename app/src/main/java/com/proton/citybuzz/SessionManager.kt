// In: com/proton/citybuzz/data/SessionManager.kt

package com.proton.citybuzz.data

import android.content.Context
import android.content.SharedPreferences
import com.proton.citybuzz.CityBuzzApp // Import your Application class
import kotlin.coroutines.coroutineContext

/**
 * Manages the user's session data (e.g., the logged-in user's ID).
 * This class uses SharedPreferences for persistence and is implemented as a singleton.
 */
class SessionManager private constructor(context: Context) {

    private val prefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "CityBuzzApp"
        private const val KEY_USER_ID = "logged_in_user_id"
        private const val KEY_USER_PASSWORD = "logged_in_user_password"

        // The single instance of SessionManager
        @Volatile
        private var INSTANCE: SessionManager? = null

        // Method to get the single instance
        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionManager(context)
                INSTANCE = instance
                instance
            }
        }
    }

    init {
        // Use the application context to prevent memory leaks
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(userId: Int, password: String) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
        prefs.edit().putString(KEY_USER_PASSWORD, password).apply()
    }

    fun getLoggedInUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun logoutUser() {
        prefs.edit().clear().apply()
    }
}
