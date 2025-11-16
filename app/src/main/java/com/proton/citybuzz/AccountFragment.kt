package com.proton.citybuzz

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proton.citybuzz.data.SessionManager
import com.proton.citybuzz.data.model.User
import com.proton.citybuzz.ui.viewmodel.SocialViewModel

class AccountFragment : Fragment(R.layout.account_page) {
    private lateinit var socialVM: SocialViewModel
    private lateinit var friendsAdapter: FriendAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        socialVM = CityBuzzApp.getInstance().socialViewModel

        val loggedInUser = socialVM.loggedInUser.value
        setUpAccountInformationUI(loggedInUser!!)
        setUpEditAccountUI(loggedInUser)
    }

    fun setUpAccountInformationUI(loggedInUser: User){

        val username = view?.findViewById<TextView>(R.id.acc_username)
        username?.text = loggedInUser.name
        val email = view?.findViewById<TextView>(R.id.acc_email)
        email?.text = loggedInUser.email
        val profilePic = view?.findViewById<ImageView>(R.id.acc_profile_pic)
        if (loggedInUser.profileImage != null) {
        profilePic?.setImageBitmap(BitmapFactory.decodeByteArray(loggedInUser.profileImage, 0,
            loggedInUser.profileImage.size
        ))}
        else {
            profilePic?.setImageResource(R.drawable.ic_account)
        }


        val rvFriends = view?.findViewById<RecyclerView>(R.id.rvFriends)
        rvFriends?.layoutManager = LinearLayoutManager(requireContext())

        friendsAdapter = FriendAdapter(
            friends = emptyList(),
            getUserName = { userId ->
                socialVM.getUserById(userId)?.name ?: "Unknown"
            },
            onRemoveFriend = { friendId ->
                socialVM.removeFriend(loggedInUser?.id ?: 0, friendId) // use friendId directly
            }
        )

        rvFriends?.adapter = friendsAdapter

        socialVM.friends.observe(viewLifecycleOwner) { friendsList ->
            friendsAdapter.updateData(friendsList)
        }

        loggedInUser?.id?.let { userId ->
            socialVM.loadFriends(userId)
        }

        val logoutButton = view?.findViewById<Button>(R.id.logout_button)
        logoutButton?.setOnClickListener {
            logOut()
        }
    }

    fun setUpEditAccountUI(loggedInUser: User?){

        val infoLayout = view?.findViewById<LinearLayout>(R.id.acc_info_layout)
        val editLayout = view?.findViewById<LinearLayout>(R.id.edit_account_layout)

        val changePassLayout = view?.findViewById<LinearLayout>(R.id.change_password_layout)

        val editNameText = view?.findViewById<EditText>(R.id.acc_edit_username)
        val editEmailText = view?.findViewById<EditText>(R.id.acc_edit_email)

        val currentPassword = view?.findViewById<EditText>(R.id.current_password)
        val newPassword = view?.findViewById<EditText>(R.id.new_password)
        val confirmPassword = view?.findViewById<EditText>(R.id.confirm_password)

        val editNameButton = view?.findViewById<ImageButton>(R.id.edit_name)
        val editEmailButton = view?.findViewById<ImageButton>(R.id.edit_email)
        val editPasswordButton = view?.findViewById<Button>(R.id.edit_password)

        var editingName = false
        var editingEmail = false
        var editingPassword = false

        editNameButton?.setOnClickListener {
            infoLayout?.visibility = View.GONE
            editLayout?.visibility = View.VISIBLE
            editNameText?.visibility = View.VISIBLE
            editingName = true
        }

        editEmailButton?.setOnClickListener {
            infoLayout?.visibility = View.GONE
            editLayout?.visibility = View.VISIBLE
            editEmailText?.visibility = View.VISIBLE
            editingEmail = false
        }

        editPasswordButton?.setOnClickListener {
            infoLayout?.visibility = View.GONE
            editLayout?.visibility = View.VISIBLE
            changePassLayout?.visibility = View.VISIBLE
            editingPassword = false
        }

        val saveChangesButton = view?.findViewById<Button>(R.id.acc_save_changes)
        saveChangesButton?.setOnClickListener {
            val inputPassword = currentPassword?.text.toString()

            val userId = loggedInUser?.id ?: 0

            if(inputPassword != loggedInUser?.password){
                Toast.makeText(requireContext(), "Incorrect password", Toast.LENGTH_SHORT).show()
            }
            else if (editingPassword && newPassword?.text.toString() != confirmPassword?.text.toString()){
                Toast.makeText(requireContext(), "Passwords don't match", Toast.LENGTH_SHORT).show()
            }
            else {
                if(editingName){
                    CityBuzzApp.getInstance().socialViewModel.updateName(userId,  editNameText?.text.toString())
                }
                if(editingEmail){
                    CityBuzzApp.getInstance().socialViewModel.updateEmail(userId,  editEmailText?.text.toString())
                }
                if(editingPassword){
                    CityBuzzApp.getInstance().socialViewModel.updatePassword(userId,  newPassword?.text.toString())
                }

                Toast.makeText(requireContext(), "Changes saved!", Toast.LENGTH_SHORT).show()
            }

            editNameText?.text?.clear()
            editEmailText?.text?.clear()
            currentPassword?.text?.clear()
            newPassword?.text?.clear()
            confirmPassword?.text?.clear()

            editNameText?.visibility = View.GONE
            editEmailText?.visibility = View.GONE
            changePassLayout?.visibility = View.GONE

            infoLayout?.visibility = View.VISIBLE
            editLayout?.visibility = View.GONE
        }
    }

    fun logOut(){
        CityBuzzApp.getInstance().socialViewModel.loggedInUser.value = null
        SessionManager.getInstance(requireContext()).logoutUser()

        parentFragmentManager.beginTransaction()
            .remove(this)
            .commit()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}