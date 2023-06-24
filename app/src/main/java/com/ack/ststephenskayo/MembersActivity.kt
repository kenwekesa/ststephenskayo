package com.ack.ststephenskayo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MembersActivity : AppCompatActivity() {

    private lateinit var signUpButton: Button
    private lateinit var paymentButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        signUpButton = findViewById(R.id.signup_btn);
        paymentButton = findViewById(R.id.payment_btn);


        signUpButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        paymentButton.setOnClickListener()
        {
            val payment_intent = Intent(this, Payment::class.java)
            startActivity(payment_intent)
        }

    }
}