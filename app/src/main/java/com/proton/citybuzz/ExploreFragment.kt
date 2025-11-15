package com.proton.citybuzz

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.proton.citybuzz.data.model.Event
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import org.w3c.dom.Text


class ExploreFragment: Fragment(R.layout.activity_explore) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            populateView()
        }
    }

    fun populateView(){
        val eventListContainer = view?.findViewById<LinearLayout>(R.id.explore_event_container)
        val inflater = LayoutInflater.from(requireContext())
        val eventList = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        setUpListView(eventList.findViewById(R.id.list_view))

        eventListContainer?.addView(eventList)
    }
    fun setUpListView(listView: ListView){
        val eventViewModel = CityBuzzApp.getInstance().eventViewModel
        val userId = CityBuzzApp.getInstance().socialViewModel.loggedInUser.value?.id ?: 0
        eventViewModel.loadSuggestedEvents(userId)

        eventViewModel.suggestedEvents.observe(viewLifecycleOwner) { events ->
            updateListView(listView, events)
        }
    }

    fun updateListView(listView: ListView, events: List<Event>) {
        val adapter = object : ArrayAdapter<Event>(requireContext(), 0, events) {
            @SuppressLint("DefaultLocale")
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.event_list_item, parent, false)

                val item = getItem(position)

                val eventName = view.findViewById<TextView>(R.id.event_name)
                val userName = view.findViewById<TextView>(R.id.user_name)
                val startTime = view.findViewById<TextView>(R.id.event_start_time)

                eventName.text = item?.title
                startTime.text = String.format("%02d:%02d", item?.date?.hour, item?.date?.minute)

                val eventLocation = view.findViewById<TextView>(R.id.event_location)
                eventLocation.text = item?.location

                val eventDescription = view.findViewById<TextView>(R.id.event_description)
                eventDescription.text = item?.description

                val joinEventButton = view?.findViewById<Button>(R.id.join_event_button)
                joinEventButton?.setOnClickListener {
                    joinToEvent(item?.id!!)
                }

                lifecycleScope.launch {
                    val eventCreator = CityBuzzApp.getInstance().socialViewModel.getUserById(item?.creatorId)?.name
                    userName.text = eventCreator
                }
                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val eventDetailsContainer = view.findViewById<LinearLayout>(R.id.event_details_container)
            val joinEventButton = view.findViewById<Button>(R.id.join_event_button)

            if (eventDetailsContainer.isVisible) {
                eventDetailsContainer.visibility = View.GONE
                joinEventButton.visibility = View.GONE
            }
            else {
                eventDetailsContainer.visibility = View.VISIBLE
                joinEventButton.visibility = View.VISIBLE
            }
        }
    }

    fun joinToEvent(eventId: Int){
        val currentUserId = CityBuzzApp.getInstance().socialViewModel.loggedInUser.value?.id
        val eventVM = CityBuzzApp.getInstance().eventViewModel
        eventVM.addAttendee(eventId, currentUserId ?: 0)
        eventVM.loadSuggestedEvents(currentUserId ?: 0)
        eventVM.loadMyEvents(currentUserId ?: 0)
    }

}