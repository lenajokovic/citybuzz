package com.proton.citybuzz

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BottomNavigationView bottomNavigation
        if (savedInstanceState == null) {
            supportFragmentManager.
        }
        setContentView(R.layout.navigation_bar)
    }
}