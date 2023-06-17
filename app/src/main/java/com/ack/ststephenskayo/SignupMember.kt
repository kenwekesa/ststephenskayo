package com.ack.ststephenskayo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignupMember : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_member)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)

        // Set click listener for the sign-up button
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            signUpUser(email, password)
        }
    }

    private fun signUpUser(email: String, password: String) {
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign up success
                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                        // Perform any additional actions upon successful sign-up
                    } else {
                        // Sign up failed
                        val errorMessage = task.exception?.message
                        Toast.makeText(this, "Sign up failed: $errorMessage", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
        catch(e:Exception)
        {
            Log.e("ST. STEPHENS KAYO", "exception: " + e.localizedMessage);
        }
    }
}
