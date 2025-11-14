package com.proton.citybuzz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.data.model.EventPrivacy
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.time.LocalDate
import java.time.LocalTime


class ExploreFragment: Fragment(R.layout.activity_explore) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateView()
    }

    fun populateView(){

        val eventListContainer = view?.findViewById<LinearLayout>(R.id.explore_event_container)
        val inflater = LayoutInflater.from(context!!)
        val eventList = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        GlobalScope.async {
            setUpListView(eventList.findViewById(R.id.list_view))
        }

        eventListContainer?.addView(eventList)
    }

    suspend fun setUpListView(listView: ListView){

        val event1 = Event(0, "First Event",
            LocalDate.of(2023, 10, 10),
            LocalTime.now(),
            "Description",
            "Location",
            EventPrivacy.PUBLIC,
            0)
        val event2 = Event(0, "Second Event",
            LocalDate.of(2023, 3, 3),
            LocalTime.NOON,
            "Description",
            "Location",
            EventPrivacy.PUBLIC,
            69)

        CityBuzzApp.eventViewModel.loadEvents()
        CityBuzzApp.eventViewModel.addEvent("First",
            "Blahblah",
            "Beograd",
            LocalDate.of(2025, 10, 15),
            LocalTime.of(12, 30),
            0,
            0)

        val events = listOf(event1, event2) //CityBuzzApp.eventViewModel.events.value

        val adapter = object : ArrayAdapter<Event>(context!!, 0, events) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false)

                val item = getItem(position)

                val profilePic = view.findViewById<ImageView>(R.id.profile_pic)
                val eventName = view.findViewById<TextView>(R.id.event_name)
                val userName = view.findViewById<TextView>(R.id.user_name)

                profilePic.setImageResource(R.drawable.ic_explore)
                eventName.text = item?.title
                GlobalScope.async {
                    userName.text = CityBuzzApp.socialViewModel.getUser(item?.creatorId).name
                }

                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            showEventDetails(events[position].userId)
        }
    }

    fun showEventDetails(event_id: Long){

    }

}