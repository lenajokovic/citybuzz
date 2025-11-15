package com.proton.citybuzz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class NetworkFragment : Fragment(R.layout.fragment_network) {

    private lateinit var rvFriendRequests: RecyclerView
    private lateinit var rvSuggestions: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views manually
        rvFriendRequests = view.findViewById(R.id.friend_requests)
        rvSuggestions = view.findViewById(R.id.suggestions)

        // LayoutManagers
        rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        rvSuggestions.layoutManager = LinearLayoutManager(requireContext())

        val socialVM = CityBuzzApp.getInstance().socialViewModel
        val userId = socialVM.loggedInUser.value?.id ?: 0L

        // Friend Requests Adapter
        val friendRequestsAdapter = RequestAdapter(
            emptyList(),
            onAccept = { socialVM.acceptRequest(it) },
            onReject  = { socialVM.rejectRequest(it) },
            getUser   = { socialVM.getUser(it.toInt())!! }
        )
        rvFriendRequests.adapter = friendRequestsAdapter

        // Suggestions Adapter
        val suggestionAdapter = SuggestionAdapter(
            emptyList(),
            onSendRequest = { target -> socialVM.sendRequest(userId.toInt(), target.id) }
        )
        rvSuggestions.adapter = suggestionAdapter

        // Observe LiveData
        socialVM.pendingRequests.observe(viewLifecycleOwner) { list ->
            friendRequestsAdapter.update(list ?: emptyList())
        }

        socialVM.suggestions.observe(viewLifecycleOwner) { list ->
            suggestionAdapter.update(list ?: emptyList())
        }

        // Load data
        socialVM.loadPendingRequests(userId.toInt())
        socialVM.loadFriendSuggestions(userId.toInt())
    }
}
