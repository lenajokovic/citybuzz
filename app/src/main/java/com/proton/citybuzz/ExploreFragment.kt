package com.proton.citybuzz

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.proton.citybuzz.data.model.isFuture
import com.proton.citybuzz.data.model.isThisWeek
import com.proton.citybuzz.data.model.isToday
import com.proton.citybuzz.data.model.isTomorrow
import com.proton.citybuzz.ui.viewmodel.EventViewModel
import com.proton.citybuzz.ui.viewmodel.SocialViewModel


private var tomorrow: Boolean = TODO("initialize me")
// In ExploreFragment.kt

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var socialViewModel: SocialViewModel // Add this
    private lateinit var eventListContainer: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize both ViewModels
        eventViewModel = CityBuzzApp.getInstance().eventViewModel
        socialViewModel = CityBuzzApp.getInstance().socialViewModel // Initialize it
        eventListContainer = view.findViewById(R.id.explore_event_container)
        eventViewModel.loadSuggestedEvents(socialViewModel.loggedInUser.value?.id ?: 0)
        observeEvents()
    }

    fun observeEvents() {
        // This function remains the same
        eventViewModel.suggestedEvents.observe(viewLifecycleOwner, Observer { allEvents ->
            eventListContainer.removeAllViews()

            val todayEvents = allEvents.filter { it.isToday() }
            if (todayEvents.isNotEmpty()) {
                populateDayList("Today", todayEvents)
            }

            val tomorrowEvents = allEvents.filter { it.isTomorrow() }
            if (tomorrowEvents.isNotEmpty()) {
                populateDayList("Tomorrow", tomorrowEvents)
            }

            val thisWeekEvents = allEvents.filter { it.isThisWeek() && !it.isToday() && !it.isTomorrow() }
            if (thisWeekEvents.isNotEmpty()) {
                populateDayList("This Week", thisWeekEvents)
            }

            val futureEvents = allEvents.filter { it.isFuture() && !it.isThisWeek() && !it.isToday() && !it.isTomorrow() }
            if (futureEvents.isNotEmpty()) {
                populateDayList("Future Events", futureEvents)
            }
        })
    }

    private fun populateDayList(title: String, events: List<Event>) {
        val inflater = LayoutInflater.from(requireContext())
        val dayListView = inflater.inflate(R.layout.day_event_list, eventListContainer, false)

        val titleTextView = dayListView.findViewById<TextView>(R.id.day_title)
        val recyclerView = dayListView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)

        titleTextView.text = title

        // Create the adapter instance with the clean callback
        val adapter = EventAdapter(
            events,
            viewLifecycleOwner.lifecycleScope,
            socialViewModel // Pass the ViewModel
        ) { event ->
            // This is the ONLY thing that should be in the callback
            joinToEvent(event.id)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Add the finished view to the container
        eventListContainer.addView(dayListView)
    }

    fun joinToEvent(eventId: Int) {
        // This function remains the same
        val currentUserId = socialViewModel.loggedInUser.value?.id
        eventViewModel.addAttendee(eventId, currentUserId ?: 0)
        eventViewModel.loadSuggestedEvents(currentUserId ?: 0)
        eventViewModel.loadMyEvents(currentUserId ?: 0)
    }

    // You can now REMOVE the unused setListViewHeightBasedOnChildren function
}
