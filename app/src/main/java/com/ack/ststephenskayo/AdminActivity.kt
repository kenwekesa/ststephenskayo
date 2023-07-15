package com.ack.ststephenskayo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.cardview.widget.CardView

class AdminActivity : AppCompatActivity() {
    private lateinit var signUpButton: CardView
    private lateinit var paymentButton: CardView
    private lateinit var membersButton: CardView
    private lateinit var membershipButton: CardView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        signUpButton = findViewById(R.id.add_members_card);
        paymentButton = findViewById(R.id.record_payment_card);
        membersButton = findViewById(R.id.members_card);
        membershipButton = findViewById((R.id.my_membership_card))



        signUpButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
        membersButton.setOnClickListener()
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