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
        rvSearchResults  = view.findViewById(R.id.search_suggestions)

        rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        rvSuggestions.layoutManager    = LinearLayoutManager(requireContext())
        rvSearchResults.layoutManager  = LinearLayoutManager(requireContext())

        val socialVM = CityBuzzApp.getInstance().socialViewModel
        val userId = socialVM.loggedInUser.value?.id ?: 0

        // CREATE ADAPTERS

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
                socialVM.sendRequest(userId, target.id)
            }
        )
        rvSuggestions.adapter = suggestionAdapter

        val searchAdapter = SuggestionAdapter(
            emptyList(),
            onSendRequest = { target ->
                socialVM.sendRequest(userId, target.id)
            }
        )
        rvSearchResults.adapter = searchAdapter

        // OBSERVE OUTGOING REQUESTS (pending)

        socialVM.outgoingRequests.observe(viewLifecycleOwner) { pending ->
            val set = pending ?: emptySet()
            suggestionAdapter.updatePendingRequests(set)
            searchAdapter.updatePendingRequests(set)
        }

        // SEARCH BUTTON

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            lifecycleScope.launch {
                if (query.isEmpty()) {
                    searchAdapter.updateUsers(emptyList())
                    return@launch
                }

                val results = socialVM.searchUsers(query)
                searchAdapter.updateUsers(results)
            }
        }

        // LIVE SEARCH

        searchInput.addTextChangedListener { text ->
            val query = text.toString().trim()
            lifecycleScope.launch {
                if (query.isEmpty()) {
                    searchAdapter.updateUsers(emptyList())
                    return@launch
                }

                val results = socialVM.searchUsers(query)

                val friends = socialVM.friends.value ?: emptyList()
                val friendIds = friends.map { it.id }

                val filtered = results.filter { user ->
                    user.id != userId && user.id !in friendIds
                }

                searchAdapter.updateUsers(filtered)
            }
        }

        // OBSERVE LIVE DATA (requests, suggestions)

        socialVM.pendingRequests.observe(viewLifecycleOwner) { list ->
            friendRequestsAdapter.update(list ?: emptyList())
        }

        socialVM.suggestions.observe(viewLifecycleOwner) { list ->
            suggestionAdapter.updateUsers(list ?: emptyList())
        }

        // INITIAL LOAD

        socialVM.loadPendingRequests(userId)
        socialVM.loadFriendSuggestions(userId)
    }
}

