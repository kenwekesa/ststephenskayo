package com.ack.ststephenskayo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView

class AdminActivity : AppCompatActivity() {
    private lateinit var signUpButton: CardView
    private lateinit var paymentButton: CardView

    private lateinit var welfarePaymentButton: CardView
    private lateinit var twentyPaymentButton: CardView

    private lateinit var menuGrid: GridLayout

    private lateinit var membersButton: CardView
    private lateinit var membershipButton: CardView
    private lateinit var manage_security_card: CardView
    private lateinit var birthdays_card: CardView
    private lateinit var statements_card: CardView


    private lateinit var username_tv: TextView



   private lateinit var sharedPrefs:SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        //val usertypee = intent.getStringExtra("usertype")

        signUpButton = findViewById(R.id.add_members_card);
        //paymentButton = findViewById(R.id.record_payment_card);
        twentyPaymentButton = findViewById(R.id.record_twenty_payment_card);
        welfarePaymentButton = findViewById(R.id.record_welfare_payment_card);
        membersButton = findViewById(R.id.members_card);
        membershipButton = findViewById((R.id.my_membership_card))
        manage_security_card = findViewById(R.id.manage_security_card)
        birthdays_card = findViewById(R.id.birthdays_card)
        statements_card = findViewById(R.id.statements_card)
        menuGrid = findViewById(R.id.menuGridLayout)


        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val phoneNumber = sharedPrefs.getString("phoneNumber", null) //
        val memberNumber = sharedPrefs.getString("memberNumber", null) //
        val firstname = sharedPrefs.getString("firstname", null)
        val lastname = sharedPrefs.getString("lastname", null)
        val usertype = sharedPrefs.getString("usertype", null)

        username_tv = findViewById(R.id.username_tv)


        username_tv.setText(firstname+" "+lastname)



        if(usertype != "twenty_admin" && usertype != "super_admin")
        {
            menuGrid.removeView(twentyPaymentButton)
          // twentyPaymentButton.visibility = View.GONE
        }
        else if(usertype != "welfare_admin" && usertype != "super_admin")
        {
            menuGrid.removeView(welfarePaymentButton)
            //welfarePaymentButton.visibility = View.GONE
        }
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

//        paymentButton.setOnClickListener()
//        {
//            val payment_intent = Intent(this, Payment::class.java)
//            startActivity(payment_intent)
//        }

        twentyPaymentButton.setOnClickListener()
        {
            val payment_intent = Intent(this, Payment::class.java)
            intent.putExtra("paymentType", "twenty")
            startActivity(payment_intent)
        }
        welfarePaymentButton.setOnClickListener()
        {
            val payment_intent = Intent(this, Payment::class.java)
            intent.putExtra("paymentType", "welfare")
            startActivity(payment_intent)
        }

        membershipButton.setOnClickListener()
        {
            //s
            val intent = Intent(this, MembersActivity::class.java)
            startActivity(intent)
        }

        birthdays_card.setOnClickListener()
        {
//            if(usertype === "secretary_admin")
//            {
            val intent = Intent(this, Birthday::class.java)
            startActivity(intent)
//            }
//            else
//            {
//                Toast.makeText(this, "You have no rights to access this side! Praise God :)", Toast.LENGTH_SHORT).show()
//
//            }
        }

        statements_card.setOnClickListener()
        {
            val intent = Intent(this, Statements::class.java)
            startActivity(intent)
        }
    }
}