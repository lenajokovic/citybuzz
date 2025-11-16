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
        val friendsListContainer: LinearLayout = itemView.findViewById(R.id.friends_list_container)

        init {
            // Expand/collapse details when item is clicked
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

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = events[position]

        holder.eventName.text = item.title
        holder.startTime.text = String.format("%02d:%02d", item.date.hour, item.date.minute)
        holder.eventLocation.text = item.location

        // Prikaz description + broj učesnika
        lifecycleScope.launch {
            val attendeeCount = eventViewModel.getAttendeeCount(item.id)
            holder.eventDescription.text = "${item.description}\nAttendees: $attendeeCount"
        }

        // Remove button samo za kreatora
        holder.removeEventButton.isVisible = item.creatorId == socialViewModel.loggedInUser.value?.id
        holder.removeEventButton.setOnClickListener {
            onRemoveClicked(item)
        }

        // Prikaz imena kreatora eventa
        lifecycleScope.launch {
            val creatorName = socialViewModel.getUserById(item.creatorId)?.name
            holder.userName.text = creatorName ?: "Unknown User"
        }

        // Invite dugme - otvara listu prijatelja direktno u holderu
        holder.inviteEventButton.setOnClickListener {
            val container = holder.friendsListContainer
            if (container.isVisible) {
                container.visibility = View.GONE
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val friends = socialViewModel.friends.value ?: emptyList()
                container.removeAllViews()
                val inflater = LayoutInflater.from(holder.itemView.context)

                friends.forEach { friend ->
                    val row = inflater.inflate(R.layout.friend_invite_item, container, false)
                    val friendName = row.findViewById<TextView>(R.id.friend_name)
                    val sendInviteBtn = row.findViewById<Button>(R.id.send_invite_btn)

                    friendName.text = friend.name
                    sendInviteBtn.setOnClickListener {
                        eventViewModel.sendEventInvite(
                            eventId = item.id,
                            fromUserId = socialViewModel.loggedInUser.value?.id ?: 0,
                            toUserId = friend.id
                        )
                        container.visibility = View.GONE
                    }

                    container.addView(row)
                }

                container.visibility = View.VISIBLE
            }
        }
    }

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }
}

