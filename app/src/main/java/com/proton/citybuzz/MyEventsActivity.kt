package com.proton.citybuzz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge


class MyEventsActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_events)
        populateView()
    }

    fun populateView(){

        val eventListContainer = findViewById<LinearLayout>(R.id.my_events_event_container)
        val inflater = LayoutInflater.from(this)
        val eventList = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        val listView = eventList.findViewById<ListView>(R.id.list_view)
        val items = listOf("Apple", "Banana", "Orange", "Grapes", "Mango")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, // built-in layout for list items
            items
        )
        listView.adapter = adapter

        eventListContainer.addView(eventList)
    }
}