package com.proton.citybuzz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.data.model.FriendRequest

class RequestAdapter(
    private var items: List<FriendRequest>,
    private val onAccept: (FriendRequest) -> Unit,
    private val onReject: (FriendRequest) -> Unit,
    //private val getUser: suspend (Long) -> User
) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = items[position]

        // Load user info (name, avatar)
        val context = holder.itemView.context
        CoroutineScope(Dispatchers.Main).launch {
            val sender = getUser(request.fromUserId)
            holder.binding.tvName.text = sender.name
        }

        holder.binding.btn_accept.setOnClickListener { onAccept(request) }
        holder.binding.btn_reject.setOnClickListener { onReject(request) }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<FriendRequest>) {
        items = newItems
        notifyDataSetChanged()
    }
}
