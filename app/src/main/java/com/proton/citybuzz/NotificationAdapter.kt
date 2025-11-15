package com.proton.citybuzz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.R
import com.proton.citybuzz.data.model.Notification
class NotificationAdapter(
    private var notifications: MutableList<Notification>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.notification_message)
        val time: TextView = itemView.findViewById(R.id.notification_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = notifications[position]
        holder.message.text = notif.message
        holder.time.text = notif.createdAt
    }

    override fun getItemCount(): Int = notifications.size

    fun update(newList: List<Notification>) {
        notifications = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int): Notification {
        val removed = notifications.removeAt(position)
        notifyItemRemoved(position)
        return removed
    }
}
