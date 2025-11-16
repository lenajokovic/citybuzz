package com.proton.citybuzz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.data.model.FriendRequest

class SuggestionAdapter(
    private var items: List<User>,
    private val onSendRequest: (User) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    var outgoingRequests: Set<Int> = emptySet()  // <- store current pending requests
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val btnAdd: Button = view.findViewById(R.id.btnConnect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = items[position]

        holder.tvName.text = user.name
        val socialVM = CityBuzzApp.getInstance().socialViewModel
        val isPending = outgoingRequests.any { it == user.id }
        holder.btnAdd.text = if (isPending) "Waiting" else "Connect"
        holder.btnAdd.isEnabled = !isPending

        holder.btnAdd.setOnClickListener {
            if(!isPending) onSendRequest(user)
        }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<User>) {
        items = newItems
        notifyDataSetChanged()
    }
}

