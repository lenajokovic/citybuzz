package com.proton.citybuzz

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.proton.citybuzz.data.model.Event
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.proton.citybuzz.data.model.isFuture
import com.proton.citybuzz.data.model.isThisWeek
import com.proton.citybuzz.data.model.isToday
import com.proton.citybuzz.data.model.isTomorrow
import com.proton.citybuzz.ui.viewmodel.EventViewModel


private var tomorrow: Boolean = TODO("initialize me")

class ExploreFragment: Fragment(R.layout.activity_explore) {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventListContainer: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventViewModel = CityBuzzApp.getInstance().eventViewModel
        eventListContainer = view.findViewById(R.id.explore_event_container)
        eventViewModel.loadEvents()
        observeEvents()
    }

    fun observeEvents(){
        eventViewModel.events.observe(viewLifecycleOwner, Observer { allEvents ->
            // Clear previous views if you're reloading data
            eventListContainer.removeAllViews()

            val todayEvents = allEvents.filter { it.isToday() }
            if (todayEvents.isNotEmpty()) {
                populateDayList("Today", todayEvents)
            }

            val tomorrowEvents = allEvents.filter { it.isTomorrow() }
            if (tomorrowEvents.isNotEmpty()) {
                populateDayList("Tomorrow", tomorrowEvents)
            }

            val weekendEvents = allEvents.filter { it.isThisWeek() && !it.isToday() && !it.isTomorrow() }
            if (weekendEvents.isNotEmpty()) {
                populateDayList("This Week", weekendEvents)
            }

            val futureEvents = allEvents.filter { it.isFuture() && !it.isThisWeek() && !it.isToday() && !it.isTomorrow() }
            if (futureEvents.isNotEmpty()) {
                populateDayList("Future Events", futureEvents)
            }
        })
    }

    private fun populateDayList(title: String, events: List<Event>) {
        val inflater = LayoutInflater.from(requireContext())
        // Inflate your day_event_list layout
        val dayListView = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        // Find the views within the inflated layout
        val titleTextView = dayListView.findViewById<TextView>(R.id.day_title)
        val listView = dayListView.findViewById<ListView>(R.id.list_view)

        // Set the title ("Today", "Tomorrow", etc.)
        titleTextView.text = title

        // Create an adapter for this specific list
        val adapter = object : ArrayAdapter<Event>(requireContext(), R.layout.event_list_item, events) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: inflater.inflate(R.layout.event_list_item, parent, false)

                val item = getItem(position)

                // Populate your event_list_item views here...
                val eventName = view.findViewById<TextView>(R.id.event_name)
                eventName.text = item?.title
                // ... setup other views like images, user names, etc.

                return view
            }
        }

        listView.adapter = adapter

        // **CRUCIAL**: Manually set the height of the ListView
        // This is required when a ListView is inside a ScrollView
        setListViewHeightBasedOnChildren(listView)

        // Add the fully populated day list view to the main container
        eventListContainer.addView(dayListView)
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }

    fun setUpListView(listView: ListView){
        val eventViewModel = CityBuzzApp.getInstance().eventViewModel
        eventViewModel.loadEvents()

        eventViewModel.events.observe(listView.findViewTreeLifecycleOwner(), { events ->
            updateListView(listView, events)
        })
    }

    fun updateListView(listView: ListView, events: List<Event>) {
        val adapter = object : ArrayAdapter<Event>(listView.context, 0, events) {
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

                listView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    val eventCreator =
                        CityBuzzApp.getInstance().socialViewModel.getUserById(item?.creatorId)?.name
                    userName.text = eventCreator
                }
                return view
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val eventDetailsContainer =
                view.findViewById<LinearLayout>(R.id.event_details_container)
            val joinEventButton = view.findViewById<Button>(R.id.join_event_button)

            if (eventDetailsContainer.isVisible) {
                eventDetailsContainer.visibility = View.GONE
                joinEventButton.visibility = View.GONE
            } else {
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