package com.proton.citybuzz

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var profile_pic: ImageView
    private lateinit var btn_select_photo: Button
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirm_password: EditText
    private lateinit var btn_create_account: Button

    private var selected_image_uri: Uri? = null

    // Activity result launcher for picking image
    private val pick_image_launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selected_image_uri = result.data?.data
            profile_pic.setImageURI(selected_image_uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account_page)

        // Link views
        profile_pic = findViewById(R.id.profile_pic)
        btn_select_photo = findViewById(R.id.btn_select_photo)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        confirm_password = findViewById(R.id.confirm_password)
        btn_create_account = findViewById(R.id.btn_create_account)

        // Select photo
        btn_select_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pick_image_launcher.launch(intent)
        }

        // Create account button
        btn_create_account.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val name = name.text.toString().trim()
        val email = email.text.toString().trim()
        val password = password.text.toString()
        val confirmPassword = confirm_password.text.toString()

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert selected image URI to string (optional, can store null)
        val profileImageString = selected_image_uri?.toString()

        CityBuzzApp.socialViewModel.addUser(name, email, password, profileImageString)

        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
        finish()
    }
}