package com.proton.citybuzz

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proton.citybuzz.ui.fragment.NotificationFragment
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
        val accountButton = findViewById<ImageButton>(R.id.show_account_button)
        val closeButton = findViewById<ImageButton>(R.id.close_button)

        val imageBitmap = CityBuzzApp.getInstance().socialViewModel.getImageBitmap()
        if (imageBitmap != null) {
            accountButton.setImageBitmap(imageBitmap)
        } else {
            accountButton.setImageResource(R.drawable.ic_account)
        }

        val exploreFragment = ExploreFragment()
        val networkFragment = NetworkFragment()
        val notificationFragment = NotificationFragment()
        val accountFragment = AccountFragment()
        val myEventsFragment = MyEventsFragment()
        replaceFragment(exploreFragment, accountButton, closeButton)

        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    replaceFragment(exploreFragment, accountButton, closeButton)
                    true
                }

                R.id.nav_network -> {
                    replaceFragment(networkFragment, accountButton, closeButton)
                    true
                }

                R.id.nav_notification -> {
                    replaceFragment(notificationFragment, accountButton, closeButton)
                    true
                }

                R.id.nav_my_events -> {
                    replaceFragment(myEventsFragment, accountButton, closeButton)
                    true
                }

                else -> false
            }
        }

        accountButton.setOnClickListener {
            replaceFragment(accountFragment, accountButton, closeButton)
            accountButton.visibility = View.GONE
            closeButton.visibility = View.VISIBLE
        }


        closeButton?.setOnClickListener {
            replaceFragment(exploreFragment, accountButton, closeButton)
        }

    }

    private fun replaceFragment(fragment: Fragment, accountButton: ImageButton, closeButton: ImageButton) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        accountButton.visibility = View.VISIBLE
        closeButton.visibility = View.GONE
    }

}