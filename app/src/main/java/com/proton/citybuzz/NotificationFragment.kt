package com.proton.citybuzz.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.CityBuzzApp
import com.proton.citybuzz.R
import com.proton.citybuzz.NotificationAdapter
import androidx.recyclerview.widget.ItemTouchHelper

class NotificationFragment : Fragment(R.layout.fragment_notification) {

    private lateinit var adapter: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = CityBuzzApp.getInstance()
        val notificationVM = app.notificationViewModel
        val eventVM = app.eventViewModel

        val rv = view.findViewById<RecyclerView>(R.id.notifications)

        adapter = NotificationAdapter(mutableListOf())
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // Observe ViewModel notifications
        notificationVM.notifications.observe(viewLifecycleOwner) { list ->
            adapter.update(list)
        }

        // Load user notifications
        val socialVM = CityBuzzApp.getInstance().socialViewModel
        val userId = socialVM.loggedInUser.value?.id ?: 0L
        notificationVM.loadNotifications(userId.toInt())

        // --- SWIPE TO DELETE ---
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removed = adapter.removeAt(position)
                notificationVM.markRead(removed.id)
            }
        })

        itemTouchHelper.attachToRecyclerView(rv)
    }
}
