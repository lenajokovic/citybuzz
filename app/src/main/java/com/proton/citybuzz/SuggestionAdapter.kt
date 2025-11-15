package com.proton.citybuzz

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

    inner class ViewHolder(val binding: ItemSuggestionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSuggestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = items[position]

        holder.binding.tvName.text = user.name
        holder.binding.btnAdd.setOnClickListener { onSendRequest(user) }
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<User>) {
        items = newItems
        notifyDataSetChanged()
    }
}

