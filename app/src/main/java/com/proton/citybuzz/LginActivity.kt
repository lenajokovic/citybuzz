package com.proton.citybuzz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proton.citybuzz.ui.viewmodel.SocialViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btn_login: Button
    private lateinit var create_account: TextView

    //private val viewModel: SocialViewModel by lazy { CityBuzzApp.socialViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        btn_login = findViewById(R.id.btnLogin)
        create_account = findViewById(R.id.tvCreateAccount)

        btn_login.setOnClickListener { loginUser() }

        create_account.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = email.text.toString().trim()
        val password = password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        CityBuzzApp.socialViewModel.login(email, password) { user ->
            if (user != null) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                // Go to main activity or home
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
