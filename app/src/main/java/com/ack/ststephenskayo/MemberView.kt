package com.ack.ststephenskayo

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.time.LocalDate

class MemberView : AppCompatActivity() {

    private lateinit var signUpButton: Button
    private lateinit var paymentButton: Button
    private lateinit var paymentStatusButton: Button

    private lateinit var nameView: TextView
    private lateinit var memberNumberView: TextView



    private lateinit var sharedPrefs: SharedPreferences

    private val paymentManager = PaymentManager()



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_view)


        // Get the SharedPreferences instance
        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved values from SharedPreferences
        val phoneNumber = sharedPrefs.getString("phoneNumber", null)
        val memberNumber = sharedPrefs.getString("memberNumber", null)
        val firstname = sharedPrefs.getString("firstname", null)
        val lastname = sharedPrefs.getString("lastname", null)



        //signUpButton = findViewById(R.id.signup_btn);
        paymentButton = findViewById(R.id.my_payments_btn);
        paymentStatusButton = findViewById(R.id.view_payments_btn);

        nameView = findViewById(R.id.name_tv)
        memberNumberView = findViewById(R.id.member_no_tv)


        // Call getWelfareStatus and provide a callback function
        paymentManager.getWelfareStatus(phoneNumber.toString()) { message ->
            // Update the UI with the received message
            runOnUiThread {
                // Example: Display the message in a TextView
                if(message.equals("Payment status: Up to date"))
                {
                    paymentStatusButton.setBackgroundColor(0XFF0F9D58.toInt())

                }
                else
                {
                    paymentStatusButton.setBackgroundColor(Color.RED)
                }
                paymentStatusButton.setText(message)
            }
        }

        nameView.setText(firstname+" "+lastname)
        memberNumberView.setText(memberNumber)



//        fun getCurrentMonth(): String {
//            val currentMonth = LocalDate.now().month
//            return currentMonth.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
//        }
//
//        fun getPaymentStatus(month: String) {
//            // Function implementation...
//        }
//
//        fun main() {
//            val currentMonth = getCurrentMonth()
//            getPaymentStatus(currentMonth)
//        }



        paymentButton.setOnClickListener()
        {
            val payment_intent = Intent(this, MemberPayment::class.java)
            startActivity(payment_intent)
        }

    }
}