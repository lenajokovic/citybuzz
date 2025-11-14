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
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.proton.citybuzz.data.model.Event
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class ExploreActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_explore)
        populateView()
    }

    fun populateView(){
        val eventListContainer = findViewById<LinearLayout>(R.id.explore_event_container)
        val inflater = LayoutInflater.from(this)
        val eventList = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        GlobalScope.async {
            setUpListView(eventList.findViewById(R.id.list_view))
        }

        eventListContainer.addView(eventList)
    }

    suspend fun setUpListView(listView: ListView){

        val eventDAO = CityBuzzApp.db.eventDao()
        var events = eventDAO.getAllEvents()
        events += Event(0, "First Event", "Description", "Location")
        events += Event(2, "Second Event", "Description", "Location")
        
        val adapter = object : ArrayAdapter<Event>(this, 0, events) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false)

                val item = getItem(position)

                val profile_pic = view.findViewById<ImageView>(R.id.profile_pic)
                val event_name = view.findViewById<TextView>(R.id.event_name)
                val user_name = view.findViewById<TextView>(R.id.user_name)

                //profile_pic.setImageResource(item?.pic ?: 0)
                event_name.text = item?.title
                //user_name.text = item?.location

                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            showEventDetails(events[position].id)
        }
    }

    fun showEventDetails(event_id: Long?){

    }

}