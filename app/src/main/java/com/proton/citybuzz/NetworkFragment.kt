package com.proton.citybuzz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.core.widget.addTextChangedListener

class NetworkFragment : Fragment(R.layout.fragment_network) {

    private lateinit var rvFriendRequests: RecyclerView
    private lateinit var rvSuggestions: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val searchButton = view.findViewById<ImageButton>(R.id.search_button)

        rvFriendRequests = view.findViewById(R.id.friend_requests)
        rvSuggestions    = view.findViewById(R.id.suggestions)

        rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        rvSuggestions.layoutManager    = LinearLayoutManager(requireContext())

        val socialVM = CityBuzzApp.getInstance().socialViewModel
        val userId = socialVM.loggedInUser.value?.id ?: 0L

        // --- CREATE ADAPTERS FIRST ---
        val friendRequestsAdapter = RequestAdapter(
            emptyList(),
            onAccept = { socialVM.acceptRequest(it) },
            onReject = { socialVM.rejectRequest(it) },
            getUser = { socialVM.getUser(it.toInt())!! }
        )
        rvFriendRequests.adapter = friendRequestsAdapter

        val suggestionAdapter = SuggestionAdapter(
            emptyList(),
            onSendRequest = { target -> socialVM.sendRequest(userId.toInt(), target.id) }
        )
        rvSuggestions.adapter = suggestionAdapter

        // --- SEARCH HANDLERS AFTER ADAPTER IS CREATED ---
        // Search button click
        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            lifecycleScope.launch {
                val results = socialVM.searchUsers(query)  // suspend function
                suggestionAdapter.update(results)
            }
        }

// Live typing search
        searchInput.addTextChangedListener { text ->
            val query = text.toString().trim()
            lifecycleScope.launch {
                val results = socialVM.searchUsers(query)
                suggestionAdapter.update(results)
            }
        }


        // --- OBSERVE LIVE DATA ---
        socialVM.pendingRequests.observe(viewLifecycleOwner) { list ->
            friendRequestsAdapter.update(list ?: emptyList())
        }

        socialVM.suggestions.observe(viewLifecycleOwner) { list ->
            suggestionAdapter.update(list ?: emptyList())
        }

        // Load initial data
        socialVM.loadPendingRequests(userId.toInt())
        socialVM.loadFriendSuggestions(userId.toInt())
    }
}
