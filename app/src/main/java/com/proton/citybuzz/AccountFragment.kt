package com.proton.citybuzz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.ui.viewmodel.SocialViewModel

class AccountFragment : Fragment(R.layout.account_page) {
    private lateinit var socialVM: SocialViewModel
    private lateinit var friendsAdapter: FriendAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        socialVM = CityBuzzApp.getInstance().socialViewModel

        val editNameButton = view.findViewById<ImageButton>(R.id.edit_name)
        val editEmailButton = view.findViewById<ImageButton>(R.id.edit_email)
        val editPasswordButton = view.findViewById<Button>(R.id.edit_password)

        val infoLayout = view.findViewById<LinearLayout>(R.id.acc_info_layout)
        val editLayout = view.findViewById<LinearLayout>(R.id.edit_account_layout)
        val changePassLayout = view.findViewById<LinearLayout>(R.id.change_password_layout)

        val editNameText = view.findViewById<EditText>(R.id.acc_edit_username)
        val editEmailText = view.findViewById<EditText>(R.id.acc_edit_email)

        val saveChangesButton = view.findViewById<Button>(R.id.acc_save_changes)

        val currentPassword = view.findViewById<EditText>(R.id.current_password)
        val newPassword = view.findViewById<EditText>(R.id.new_password)
        val confirmPassword = view.findViewById<EditText>(R.id.confirm_password)

        val rvFriends = view.findViewById<RecyclerView>(R.id.rvFriends)
        rvFriends.layoutManager = LinearLayoutManager(requireContext())

        friendsAdapter = FriendAdapter(
            friends = emptyList(),
            getUserName = { userId ->
                socialVM.getUser(userId)?.name ?: "Unknown"
            },
            onRemoveFriend = { friendId ->
                val userId = socialVM.loggedInUser.value?.id ?: 0L
                socialVM.removeFriend(userId.toInt(), friendId) // use friendId directly
            }
        )

        rvFriends.adapter = friendsAdapter


        editNameButton.setOnClickListener {
            infoLayout.visibility = View.GONE
            editLayout.visibility = View.VISIBLE
            editNameText.visibility = View.VISIBLE
        }

        editEmailButton.setOnClickListener {
            infoLayout.visibility = View.GONE
            editLayout.visibility = View.VISIBLE
            editEmailText.visibility = View.VISIBLE
        }

        editPasswordButton.setOnClickListener {
            infoLayout.visibility = View.GONE
            editLayout.visibility = View.VISIBLE
            changePassLayout.visibility = View.VISIBLE
        }

        saveChangesButton.setOnClickListener {
            val inputPassword = currentPassword.text.toString()
            // TODO check if inputPass == password in database

            editNameText.visibility = View.GONE
            editEmailText.visibility = View.GONE
            changePassLayout.visibility = View.GONE

            infoLayout.visibility = View.VISIBLE
            editLayout.visibility = View.GONE
        }
    }
}