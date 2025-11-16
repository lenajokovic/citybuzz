package com.proton.citybuzz

import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.View
import android.widget.ImageButton
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proton.citybuzz.ui.fragment.NotificationFragment
import kotlinx.coroutines.launch


class MainActivity: AppCompatActivity() {


    private lateinit var lastFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            // SnowflakeCaller.getInstance().createConnection()
        }

        val accountFragment = AccountFragment()
        val exploreFragment = ExploreFragment()
        val networkFragment = NetworkFragment()
        val notificationFragment = NotificationFragment()
        val myEventsFragment = MyEventsFragment()

        val accountButton = findViewById<ImageButton>(R.id.show_account_button)
        val closeButton = findViewById<ImageButton>(R.id.close_button)

        setupNavigationBar(exploreFragment, notificationFragment, networkFragment, myEventsFragment, accountButton, closeButton)

        val imageBitmap = CityBuzzApp.getInstance().socialViewModel.getImageBitmap()
        if (imageBitmap != null) {
            accountButton.setImageBitmap(imageBitmap)
        } else {
            accountButton.setImageResource(R.drawable.ic_account)
        }

        accountButton.setOnClickListener {
            replaceFragment(accountFragment, accountButton, closeButton, "Account")
            accountButton.visibility = View.GONE
            closeButton.visibility = View.VISIBLE
        }


        closeButton?.setOnClickListener {
            replaceFragment(exploreFragment, accountButton, closeButton, "All Events")
        }
    }

    private fun setupNavigationBar(exploreFragment: ExploreFragment, notificationFragment: NotificationFragment, networkFragment: NetworkFragment, myEventsFragment: MyEventsFragment, accountButton: ImageButton, closeButton: ImageButton) {
        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_bar)

        replaceFragment(exploreFragment, accountButton, closeButton, "All Events")

        navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    replaceFragment(exploreFragment, accountButton, closeButton, "All Events")
                    true
                }

                R.id.nav_network -> {
                    replaceFragment(networkFragment, accountButton, closeButton, "Network")
                    true
                }

                R.id.nav_notification -> {
                    replaceFragment(notificationFragment, accountButton, closeButton, "Notifications")
                    true
                }

                R.id.nav_my_events -> {
                    replaceFragment(myEventsFragment, accountButton, closeButton, "My Events")
                    true
                }

                else -> false
            }
        }

    }

    private fun replaceFragment(fragment: Fragment, accountButton: ImageButton, closeButton: ImageButton, fragmentName: String = "") {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        accountButton.visibility = View.VISIBLE
        closeButton.visibility = View.GONE

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
        toolbar.title = fragmentName
        if(fragmentName == "Account")
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        else
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.fragment_background))

        if (fragment !is AccountFragment) {
            lastFragment = fragment
        }
    }

}