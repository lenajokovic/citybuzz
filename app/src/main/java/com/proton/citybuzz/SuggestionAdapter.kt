package com.proton.citybuzz

import android.graphics.BitmapFactory
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

    private var outgoingRequests: Set<Int> = emptySet()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val ivProfile: ImageView = view.findViewById(R.id.ivProfile)
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

        val isPending = outgoingRequests.contains(user.id)

        holder.btnAdd.text = if (isPending) "Waiting" else "Connect"
        holder.btnAdd.isEnabled = !isPending

        holder.btnAdd.setOnClickListener {
            if (!isPending) onSendRequest(user)
        }

        if (user.profileImage == null) {
            holder.ivProfile.setImageResource(R.drawable.ic_account)
        } else {
            holder.ivProfile.setImageBitmap(
                BitmapFactory.decodeByteArray(
                    user.profileImage,
                    0,
                    user.profileImage.size
                )
            )
        }
    }

    override fun getItemCount() = items.size

    fun updateUsers(newItems: List<User>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updatePendingRequests(newPending: Set<Int>) {
        outgoingRequests = newPending
        notifyDataSetChanged()
    }
}


