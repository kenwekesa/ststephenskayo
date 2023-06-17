package com.ack.ststephenskayo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MembersActivity : AppCompatActivity() {

    private lateinit var signUpButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        signUpButton = findViewById(R.id.signup_btn)


        signUpButton.setOnClickListener()
        {
            //
            val intent = Intent(this, SignupMember::class.java)
            startActivity(intent)
        }

    }
}