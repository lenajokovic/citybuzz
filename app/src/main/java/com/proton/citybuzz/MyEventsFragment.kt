package com.proton.citybuzz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.proton.citybuzz.data.model.Event
import kotlinx.coroutines.launch


class MyEventsFragment: Fragment(R.layout.activity_my_events) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateView()
    }

    fun populateView() {
        val eventListContainer = view?.findViewById<LinearLayout>(R.id.my_events_container)
        val inflater = LayoutInflater.from(requireContext())
        val eventList = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        lifecycleScope.launch {
            setUpListView(eventList.findViewById(R.id.list_view))
        }

        eventListContainer?.addView(eventList)

        val createEventButton = view?.findViewById<ImageButton>(R.id.create_event_button)
        createEventButton?.setOnClickListener {
            createEvent()
        }
    }

    fun createEvent(){
        val intent = Intent(requireContext(), CreateEventActivity::class.java)
        startActivity(intent)
    }

    fun setUpListView(listView: ListView) {
        val eventViewModel = CityBuzzApp.getInstance().eventViewModel
        val userId = CityBuzzApp.getInstance().socialViewModel.loggedInUser.value?.id
        eventViewModel.loadMyEvents(userId!!)

        eventViewModel.myEvents.observe(viewLifecycleOwner, { events ->
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

                val joinEventButton = view?.findViewById<Button>(R.id.join_event_button)
                joinEventButton?.setOnClickListener {
                    // TODO LENOOOOOOOOOOOOOOOOOOOOOOO
                }


                profilePic.setImageResource(R.drawable.ic_explore)
                eventName.text = item?.title
                lifecycleScope.launch {
                    val eventCreator = CityBuzzApp.getInstance().socialViewModel.getUser(item?.creatorId)?.name
                    userName.text = eventCreator
                }
                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val inviteContainer = view.findViewById<LinearLayout>(R.id.event_invite_container)

            if(inviteContainer.visibility == View.VISIBLE)
                inviteContainer.visibility = View.GONE
            else
                inviteContainer.visibility = View.VISIBLE
        }
    }
}