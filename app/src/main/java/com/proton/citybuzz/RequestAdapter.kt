package com.proton.citybuzz

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.data.model.FriendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequestAdapter(
    private var items: List<FriendRequest>,
    private val onAccept: (FriendRequest) -> Unit,
    private val onReject: (FriendRequest) -> Unit,
    private val getUser: suspend (Long) -> com.proton.citybuzz.data.model.User
) : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProfile: ImageView = view.findViewById(R.id.ivProfile)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvHeadline: TextView = view.findViewById(R.id.tvHeadline)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnIgnore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = items[position]

        // Load user info (name, avatar)
        val context = holder.itemView.context
        CoroutineScope(Dispatchers.Main).launch {
            val sender = getUser(request.fromUserId.toLong())
            holder.tvName.text = sender.name
            if (sender.profileImage == null) {
                holder.ivProfile.setImageResource(R.drawable.ic_account)
            } else {
                holder.ivProfile.setImageBitmap(BitmapFactory.decodeByteArray(sender.profileImage, 0,
                    sender.profileImage.size))
            }
        }

        holder.btnAccept.setOnClickListener { onAccept(request) }
        holder.btnReject.setOnClickListener { onReject(request) }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<FriendRequest>) {
        items = newItems
        notifyDataSetChanged()
    }
}
