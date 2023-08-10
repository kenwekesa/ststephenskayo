package com.ack.ststephenskayo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

class ViewMember : AppCompatActivity() {

    private lateinit var name_view:TextView
    private lateinit var fellowship_view:TextView
    private lateinit var totalpaid_view:TextView
    private lateinit var datejoined_view:TextView
    private lateinit var phone_view:TextView

    private lateinit var updateuser:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_member)

        phone_view = findViewById(R.id.phone_view)
        name_view = findViewById(R.id.name_view)
        datejoined_view=findViewById(R.id.datejoined_view)
        fellowship_view = findViewById(R.id.fellowship_view)
        totalpaid_view = findViewById(R.id.total_paid_view)
        updateuser = findViewById(R.id.update_user)

        // Retrieve the data sent from the previous activity
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val memberName = intent.getStringExtra("memberName")
        val memberDateJoined = intent.getStringExtra("memberDateJoined")
        val memberTotalPaid = intent.getDoubleExtra("memberTotalPaid", 0.0)
        val fellowship = intent.getStringExtra("fellowship")

        name_view.text = memberName
        datejoined_view.text = memberDateJoined
        totalpaid_view.text = memberTotalPaid.toString()
        phone_view.text = phoneNumber.toString()
        fellowship_view.text = fellowship.toString()


        updateuser.setOnClickListener()
        {
            val intent = Intent(this@ViewMember, UpdateUser::class.java)
            intent.putExtra("phoneNumber", phoneNumber)

            startActivity(intent)
        }

    }
}