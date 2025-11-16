// In your EventAdapter.kt file
// If you don't have this file, create it.

package com.proton.citybuzz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.data.model.Event
import com.proton.citybuzz.ui.viewmodel.EventViewModel
import com.proton.citybuzz.ui.viewmodel.SocialViewModel
import kotlinx.coroutines.launch

class EventAdapter(
    private var events: List<Event>,
    private val lifecycleScope: LifecycleCoroutineScope,
    // Pass the ViewModel to the adapter
    private val socialViewModel: SocialViewModel,
    private val eventViewModel: EventViewModel,
    private val onJoinClicked: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // This class holds the views for a single list item
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val startTime: TextView = itemView.findViewById(R.id.event_start_time)
        val eventLocation: TextView = itemView.findViewById(R.id.event_location)
        val eventDescription: TextView = itemView.findViewById(R.id.event_description)
        val attendeeCount: TextView = itemView.findViewById(R.id.attendee_count)
        val eventDetailsContainer: LinearLayout = itemView.findViewById(R.id.event_details_container)
        val joinEventButton: Button = itemView.findViewById(R.id.join_event_button)

        init {
            // Set the click listener for the whole item to expand/collapse details
            itemView.setOnClickListener {
                val isVisible = eventDetailsContainer.isVisible
                eventDetailsContainer.visibility = if (isVisible) View.GONE else View.VISIBLE
                joinEventButton.visibility = eventDetailsContainer.visibility
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_list_item, parent, false)
        return EventViewHolder(view)
    }

    override fun getItemCount(): Int = events.size

    // THIS IS THE CORRECTED METHOD
    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = events[position]

        // --- Bind all the data to the views in the ViewHolder ---
        holder.eventName.text = item.title
        holder.startTime.text = String.format("%02d:%02d", item.date.hour, item.date.minute)
        holder.eventLocation.text = item.location
        holder.eventDescription.text = item.description

        lifecycleScope.launch {
            val attendeeCount = eventViewModel.getAttendeeCount(item.id)
            holder.attendeeCount.text = "$attendeeCount people attending"
        }

        // Set the click listener for the "Join Event" button
        holder.joinEventButton.setOnClickListener {
            onJoinClicked(item)
        }

        // Fetch and set the event owner's name using the ViewModel
        lifecycleScope.launch {
            val eventCreator = socialViewModel.getUserById(item.creatorId)?.name
            holder.userName.text = eventCreator ?: "Unknown User" // Provide a fallback
        }
    }

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }
}
