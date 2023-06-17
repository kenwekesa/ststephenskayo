package com.ack.ststephenskayo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ack.ststephenskayo.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
       // binding = ActivityLoginBinding.inflate(layoutInflater, R.layout.activity_login, null)

        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        binding.login_btn.setOnClickListener {
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Call the login function
                login(username, password)
            } else {
                // Username or password is empty, display an error message
                // TODO: Handle empty username or password
            }
        }
    }

    private fun login(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, navigate to the main activity or perform desired actions
                    val user = auth.currentUser
                    // TODO: Handle successful login
                } else {
                    // Login error, display an error message or perform error handling
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidUserException -> {
                            // Invalid user email
                            // TODO: Handle invalid user
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid user password
                            // TODO: Handle invalid password
                        }
                        else -> {
                            // Other login error
                            // TODO: Handle other login error
                        }
                    }
                }
            }
    }
}
