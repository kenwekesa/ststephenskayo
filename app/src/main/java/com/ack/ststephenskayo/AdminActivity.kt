package com.ack.ststephenskayo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView

class AdminActivity : AppCompatActivity() {
    private lateinit var signUpButton: CardView
    private lateinit var paymentButton: CardView
    private lateinit var membersButton: CardView
    private lateinit var membershipButton: CardView
    private lateinit var manage_security_card: CardView
    private lateinit var username_tv: TextView



   private lateinit var sharedPrefs:SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        signUpButton = findViewById(R.id.add_members_card);
        paymentButton = findViewById(R.id.record_payment_card);
        membersButton = findViewById(R.id.members_card);
        membershipButton = findViewById((R.id.my_membership_card))
        manage_security_card = findViewById(R.id.manage_security_card)

        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val phoneNumber = sharedPrefs.getString("phoneNumber", null)
        val memberNumber = sharedPrefs.getString("memberNumber", null)
        val firstname = sharedPrefs.getString("firstname", null)
        val lastname = sharedPrefs.getString("lastname", null)

        username_tv = findViewById(R.id.username_tv)


        username_tv.setText(firstname+" "+lastname)

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