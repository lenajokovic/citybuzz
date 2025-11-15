package com.proton.citybuzz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch


class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            // SnowflakeCaller.getInstance().createConnection()
        }
        setupNavigationBar()
    }

    private fun setupNavigationBar() {
        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_bar)

        val exploreFragment = ExploreFragment()
        val myEventsFragment = MyEventsFragment()
        replaceFragment(exploreFragment)
        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    replaceFragment(exploreFragment)
                    true
                }

                R.id.nav_my_events -> {
                    replaceFragment(myEventsFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}