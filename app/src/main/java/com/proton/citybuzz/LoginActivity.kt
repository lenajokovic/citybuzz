package com.proton.citybuzz

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.proton.citybuzz.data.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btn_login: Button
    private lateinit var create_account: TextView

    //private val viewModel: SocialViewModel by lazy { CityBuzzApp.socialViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        lifecycleScope.launch { loadUserFromSession() }
    }
    fun setupUI() {
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btn_login = findViewById(R.id.btn_login)
        create_account = findViewById(R.id.create_account)

        btn_login.setOnClickListener { loginUser() }

        create_account.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    suspend private fun loadUserFromSession() {
        val sessionUserId = SessionManager.getInstance(this).getLoggedInUserId()
        val socialViewModel = CityBuzzApp.getInstance().socialViewModel

        if (sessionUserId != -1) {
            val user = socialViewModel.getUserById(sessionUserId)
            if (user != null) {
                socialViewModel.loggedInUser.postValue(user)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                SessionManager.getInstance(this).logoutUser()
                setupUI()
            }
        } else {
            setupUI()
            SnowflakeCaller.getInstance().createConnection()
        }
    }

    private fun loginUser() {
        val email = email.text.toString().trim()
        val password = password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        setContentView(R.layout.activity_loading)
        CityBuzzApp.getInstance().socialViewModel.login(email, password) { user ->
            if (user != null) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                SessionManager.getInstance(this).saveUser(user.id, user.password)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                setupUI()
            }
        }
    }

}
