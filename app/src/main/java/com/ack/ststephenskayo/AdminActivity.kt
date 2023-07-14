package com.ack.ststephenskayo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdminActivity : AppCompatActivity() {
    private lateinit var signUpButton: Button
    private lateinit var paymentButton: Button
    private lateinit var paymentStatusButton: Button
    private lateinit var membershipButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        signUpButton = findViewById(R.id.signup_btn);
        paymentButton = findViewById(R.id.payment_btn);
        paymentStatusButton = findViewById(R.id.view_payments_btn);
        membershipButton = findViewById((R.id.view_my_membership))



        signUpButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
        paymentStatusButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, MemberPayment::class.java)
            startActivity(intent)
        }

        paymentButton.setOnClickListener()
        {
            val payment_intent = Intent(this, Payment::class.java)
            startActivity(payment_intent)
        }

        membershipButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, MembersActivity::class.java)
            startActivity(intent)
        }

    }
}