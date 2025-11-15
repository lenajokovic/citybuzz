package com.proton.citybuzz

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.data.model.FriendRequest
import com.proton.citybuzz.data.model.Suggestion

class NetworkFragment : Fragment() {

    private lateinit var binding: FragmentNetworkBinding
    val viewModel = CityBuzzApp.socialViewModel
    val userId = viewModel.loggedInUser.value?.id ?: 0L

    private lateinit var requestAdapter: RequestAdapter
    private lateinit var suggestionAdapter: SuggestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNetworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = viewModel.loggedInUser.value ?: return
        val userId = user.id

        // Init adapters
        requestAdapter = RequestAdapter(
            emptyList(),
            onAccept = { viewModel.acceptRequest(it) },
            onReject  = { viewModel.rejectRequest(it) },
            getUser = { viewModel.getUser(it) }
        )

        suggestionAdapter = SuggestionAdapter(
            emptyList(),
            onSendRequest = { target ->
                viewModel.sendRequest(userId, target.id)
            }
        )

        binding.rvFriendRequests.adapter = requestAdapter
        binding.rvSuggestions.adapter = suggestionAdapter

        // Observers
        viewModel.pendingRequests.observe(viewLifecycleOwner) {
            requestAdapter.update(it ?: emptyList())
        }

        viewModel.suggestions.observe(viewLifecycleOwner) {
            suggestionAdapter.update(it ?: emptyList())
        }

        // Load data
        viewModel.loadPendingRequests(userId)
        viewModel.loadFriendSuggestions(userId)
    }
}
