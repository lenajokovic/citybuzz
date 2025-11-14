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
import androidx.fragment.app.Fragment


class MyEventsFragment: Fragment(R.layout.activity_my_events) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateView()
    }

    fun populateView(){

        val eventListContainer = view?.findViewById<LinearLayout>(R.id.my_events_event_container)
        val inflater = LayoutInflater.from(context!!)
        val eventList = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        val listView = eventList.findViewById<ListView>(R.id.list_view)
        val items = listOf("Apple", "Banana", "Orange", "Grapes", "Mango")
        val adapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_list_item_1, // built-in layout for list items
            items
        )
        listView.adapter = adapter

        eventListContainer?.addView(eventList)
    }
}