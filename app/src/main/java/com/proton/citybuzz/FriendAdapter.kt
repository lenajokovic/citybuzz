package com.proton.citybuzz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendAdapter(
    private var friends: List<Int>,
    private val getUserName: suspend (Int) -> String,
    private val onRemoveFriend: (Int) -> Unit
) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfile: ImageView = itemView.findViewById(R.id.ivFriendProfile)
        val tvName: TextView = itemView.findViewById(R.id.tvFriendName)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemoveFriend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userId = friends[position]

        // Fetch the name from SocialViewModel asynchronously
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
            val name = getUserName(userId)
            holder.tvName.text = name
        }

        holder.btnRemove.setOnClickListener {
            onRemoveFriend(userId)
        }
    }

    override fun getItemCount() = friends.size

    fun updateList(newList: List<Int>) {
        friends = newList
        notifyDataSetChanged()
    }
}
