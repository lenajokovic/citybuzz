package com.proton.citybuzz

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
        eventViewModel.loadEvents()

        eventViewModel.events.observe(viewLifecycleOwner, { events ->
            updateListView(listView, events)
        })
    }

    fun updateListView(listView: ListView, events: List<Event>) {
        val adapter = object : ArrayAdapter<Event>(requireContext(), 0, events) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.event_list_item, parent, false)

                val item = getItem(position)

                val profilePic = view.findViewById<ImageView>(R.id.profile_pic)
                val eventName = view.findViewById<TextView>(R.id.event_name)
                val userName = view.findViewById<TextView>(R.id.user_name)

                profilePic.setImageResource(R.drawable.ic_explore)
                eventName.text = item?.title
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

            if (eventDetailsContainer.visibility == View.VISIBLE) {
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
        CityBuzzApp.getInstance().eventViewModel.addAttendee(eventId, currentUserId ?: 0)
    }

}