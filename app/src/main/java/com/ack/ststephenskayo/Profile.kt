package com.ack.ststephenskayo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Profile : AppCompatActivity() {

    private lateinit var sharedPrefs:SharedPreferences

    private lateinit var name_view: TextView
    private lateinit var fellowship_view: TextView
    private lateinit var dateJoined_view: TextView
    private lateinit var phone_view: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved values from SharedPreferences
        val phoneNumber = sharedPrefs.getString("phoneNumber", null) as String
        val memberNumber = sharedPrefs.getString("memberNumber", null)
        val firstname = sharedPrefs.getString("firstname", null)
        val lastname = sharedPrefs.getString("lastname", null)
        val fellowshipg = sharedPrefs.getString("fellowship", null)
        val dateJoined = sharedPrefs.getString("dataJoined", null)

        phone_view = findViewById(R.id.phone)
        fellowship_view = findViewById(R.id.fellowship)
        dateJoined_view = findViewById(R.id.date_joined)
        name_view = findViewById(R.id.member_name)


        phone_view.setText(phoneNumber)
        fellowship_view.setText(fellowshipg)
        dateJoined_view.setText(dateJoined)
        name_view.setText(firstname+" "+lastname)


    }
}