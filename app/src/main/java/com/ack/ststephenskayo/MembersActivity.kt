package com.ack.ststephenskayo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView

class MembersActivity : AppCompatActivity() {
    private lateinit var my_profile_card: CardView
    private lateinit var my_welfare_payments_card: CardView
    private lateinit var my_welfare_status_card: CardView
    private lateinit var leaders_card: CardView
    private lateinit var my_twenty_status_card: CardView
    private lateinit var my_twenty_payments_card: CardView
    private lateinit var welfare_constitution_card: CardView
    private lateinit var calendar_of_events_card: CardView




    private lateinit var username_view: TextView



    private lateinit var sharedPrefs: SharedPreferences




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        my_profile_card = findViewById(R.id.my_profile_card);
        my_welfare_payments_card = findViewById(R.id.my_welfare_payments_card);
        my_welfare_status_card = findViewById(R.id.my_welfare_status_card);
        leaders_card = findViewById(R.id.leaders_card)
        my_twenty_payments_card = findViewById(R.id.my_twenty_payments_card)
        my_twenty_status_card = findViewById(R.id.my_twenty_status_card)
        welfare_constitution_card = findViewById(R.id.welfare_constitution_card)
        calendar_of_events_card = findViewById(R.id.calendar_of_events_card)


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
        my_welfare_payments_card.setOnClickListener()
        {
            val payment_intent = Intent(this, MemberPayment::class.java)
            payment_intent.putExtra("payment_type", "welfare")

            startActivity(payment_intent)
        }

        my_welfare_status_card.setOnClickListener()
        {
            //s
            val intent = Intent(this, MemberView::class.java)
            intent.putExtra("payment_type", "welfare")
            startActivity(intent)
        }

        my_profile_card.setOnClickListener()
        {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        my_twenty_status_card.setOnClickListener()
        {

            val intent = Intent(this, MemberView::class.java)
            intent.putExtra("payment_type", "twenty")

            startActivity(intent)

        }

        my_twenty_payments_card.setOnClickListener()
        {
            val payment_intent = Intent(this, MemberPayment::class.java)
            payment_intent.putExtra("payment_type", "twenty")

            startActivity(payment_intent)
        }
        welfare_constitution_card.setOnClickListener()
        {
            val intent = Intent(this, WelfareConstitution::class.java)
            startActivity(intent)
        }

        calendar_of_events_card.setOnClickListener()
        {
            val intent = Intent(this, CalendarOfEvents::class.java)
            startActivity(intent)
        }



    }
}