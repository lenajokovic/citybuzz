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

class EventInviteAdapter(
    private var events: List<Event>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val socialViewModel: SocialViewModel,
    private val eventViewModel: EventViewModel,
    private val onJoinClicked: (Event) -> Unit,
    private val onRemoveClicked: (Event) -> Unit
) : RecyclerView.Adapter<EventInviteAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.event_name)
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val startTime: TextView = itemView.findViewById(R.id.event_start_time)
        val eventLocation: TextView = itemView.findViewById(R.id.event_location)
        val eventDescription: TextView = itemView.findViewById(R.id.event_description)
        val eventDetailsContainer: LinearLayout = itemView.findViewById(R.id.event_details_container)
        val inviteEventButton: Button = itemView.findViewById(R.id.invite_button)
        val removeEventButton: Button = itemView.findViewById(R.id.remove_button)


        init {
            itemView.setOnClickListener {
                val isVisible = eventDetailsContainer.isVisible
                eventDetailsContainer.visibility = if (isVisible) View.GONE else View.VISIBLE
                inviteEventButton.visibility = eventDetailsContainer.visibility
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

        //holder.eventDescription.text = item.description
        lifecycleScope.launch {
            val attendeeCount = eventViewModel.getAttendeeCount(item.id)
            holder.eventDescription.text = "${item.description}\nAttendees: $attendeeCount"
        }

        // Set the click listener for the "Join Event" button
        holder.inviteEventButton.setOnClickListener {
            onJoinClicked(item)
        }

        holder.removeEventButton.isVisible = item.creatorId == socialViewModel.loggedInUser.value?.id
        holder.removeEventButton.setOnClickListener {
            onRemoveClicked(item)
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
