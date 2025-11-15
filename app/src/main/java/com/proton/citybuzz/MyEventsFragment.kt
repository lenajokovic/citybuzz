package com.proton.citybuzz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
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

    fun populateView(){

        val eventListContainer = view?.findViewById<LinearLayout>(R.id.explore_event_container)
        val inflater = LayoutInflater.from(context!!)
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

    suspend fun setUpListView(listView: ListView){
        val events = SnowflakeCaller.getInstance().getEvents()

        val adapter = object : ArrayAdapter<Event>(context!!, 0, events) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false)

                val item = getItem(position)

                val profilePic = view.findViewById<ImageView>(R.id.profile_pic)
                val eventName = view.findViewById<TextView>(R.id.event_name)
                val userName = view.findViewById<TextView>(R.id.user_name)

                profilePic.setImageResource(R.drawable.ic_explore)
                eventName.text = item?.title
                lifecycleScope.launch {
                    //userName.text = CityBuzzApp.socialViewModel.getUser(item?.creatorId).name
                }
                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            showEventDetails(events[position].id)
        }
    }

    fun showEventDetails(event_id: Int){

    }
}