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

    private lateinit var rvSearchResults: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val searchButton = view.findViewById<ImageButton>(R.id.search_button)

        rvFriendRequests = view.findViewById(R.id.friend_requests)
        rvSuggestions    = view.findViewById(R.id.suggestions)
        rvSearchResults = view.findViewById(R.id.search_suggestions)

        rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        rvSuggestions.layoutManager    = LinearLayoutManager(requireContext())

        val socialVM = CityBuzzApp.getInstance().socialViewModel
        val userId = socialVM.loggedInUser.value?.id ?: 0L

        // --- CREATE ADAPTERS FIRST ---
        val friendRequestsAdapter = RequestAdapter(
            emptyList(),
            onAccept = { socialVM.acceptRequest(it) },
            onReject = { socialVM.rejectRequest(it) },
            getUser = { socialVM.getUserById(it.toInt())!! }
        )
        rvFriendRequests.adapter = friendRequestsAdapter

        val suggestionAdapter = SuggestionAdapter(
            emptyList(),
            onSendRequest = { target ->
                socialVM.sendRequest(userId.toInt(), target.id)
                //socialVM.removeSuggestion(target)
            }
        )
        rvSuggestions.adapter = suggestionAdapter

        val searchAdapter = SuggestionAdapter(
            emptyList(),
            onSendRequest = { target ->
                socialVM.sendRequest(userId.toInt(), target.id)
                //socialVM.removeSuggestion(target)
            }
        )
        rvSearchResults.adapter = searchAdapter

        // --- OBSERVE PENDING REQUESTS ---
        socialVM.outgoingRequests.observe(viewLifecycleOwner) { outgoing ->
            val outgoingSet = outgoing ?: emptySet()
            suggestionAdapter.outgoingRequests = outgoingSet
            suggestionAdapter.notifyDataSetChanged()

            searchAdapter.outgoingRequests = outgoingSet
            searchAdapter.notifyDataSetChanged()
        }

        // --- SEARCH HANDLERS AFTER ADAPTER IS CREATED ---
        // Search button click
        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            lifecycleScope.launch {
                if (query.isEmpty()) {
                    searchAdapter.update(emptyList())   // CLEAR RESULTS
                    return@launch
                }

                val results = socialVM.searchUsers(query)
                searchAdapter.update(results)
            }
        }

// Live typing search
        searchInput.addTextChangedListener { text ->
            val query = text.toString().trim()
            lifecycleScope.launch {
                val results = socialVM.searchUsers(query)
                val currentUserId = userId.toInt()
                socialVM.loadFriends(userId.toInt())
                val friendIds = socialVM.friends.value?.map { it.id } ?: emptyList()

                val filtered = results.filter { user ->
                    user.id != currentUserId &&       // hide yourself
                            user.id !in friendIds            // hide existing friends
                }
                searchAdapter.update(filtered)
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
