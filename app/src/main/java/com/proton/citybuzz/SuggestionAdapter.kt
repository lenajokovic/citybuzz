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

class SuggestionAdapter(
    private var items: List<User>,
    private val onSendRequest: (User) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val ivProfile = view.findViewById<ImageView>(R.id.ivProfile)
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
        if (user.profileImage == null) {
            holder.ivProfile.setImageResource(R.drawable.ic_account)
        } else {
            holder.ivProfile.setImageBitmap(BitmapFactory.decodeByteArray(user.profileImage, 0,
                user.profileImage.size))
        }
        holder.btnAdd.setOnClickListener { onSendRequest(user) }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<User>) {
        items = newItems
        notifyDataSetChanged()
    }
}

