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
    private lateinit var manage_security_card: CardView





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        signUpButton = findViewById(R.id.add_members_card);
        paymentButton = findViewById(R.id.record_payment_card);
        membersButton = findViewById(R.id.members_card);
        membershipButton = findViewById((R.id.my_membership_card))
        manage_security_card = findViewById(R.id.manage_security_card)



        manage_security_card.setOnClickListener()
        {
            val intent = Intent(this, PasswordManager::class.java)
            startActivity(intent)
        }
        signUpButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
        membersButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, AllMembers::class.java)
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