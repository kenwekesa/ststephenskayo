package com.ack.ststephenskayo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView

class MembersActivity : AppCompatActivity() {
    private lateinit var my_profile_card: CardView
    private lateinit var my_payments_card: CardView
    private lateinit var my_status_card: CardView
    private lateinit var leaders_card: CardView

    private lateinit var username_view: TextView



    private lateinit var sharedPrefs: SharedPreferences




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        my_profile_card = findViewById(R.id.my_profile_card);
        my_payments_card = findViewById(R.id.my_payment_card);
        my_status_card = findViewById(R.id.my_status_card);
        leaders_card = findViewById(R.id.leaders_card)
        username_view = findViewById(R.id.user_name_view)

        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved values from SharedPreferences
        val phoneNumber = sharedPrefs.getString("phoneNumber", null)
        val memberNumber = sharedPrefs.getString("memberNumber", null)
        val firstname = sharedPrefs.getString("firstname", null)
        val lastname = sharedPrefs.getString("lastname", null)



      username_view.setText(firstname+" "+lastname)


        leaders_card.setOnClickListener()
        {
            val payment_intent = Intent(this, Leaders::class.java)
            startActivity(payment_intent)
        }
        my_payments_card.setOnClickListener()
        {
            val payment_intent = Intent(this, MemberPayment::class.java)
            startActivity(payment_intent)
        }

        my_status_card.setOnClickListener()
        {
            //s
            val intent = Intent(this, MemberView::class.java)
            startActivity(intent)
        }

    }
}