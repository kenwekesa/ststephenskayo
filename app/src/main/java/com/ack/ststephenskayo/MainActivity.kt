package com.ack.ststephenskayo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView as CardView1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle("")

        val guest_card = findViewById(R.id.guests_card) as CardView1
        val members_card = findViewById(R.id.members_card) as CardView1
        val liturgy_card = findViewById(R.id.liturgy_card) as CardView1
        val hymn_card = findViewById(R.id.hymn_card) as CardView1

        guest_card.setOnClickListener()
        {
            val intent = Intent(this, GuestActivity::class.java)
            startActivity(intent)
        }
        hymn_card.setOnClickListener()
        {
            val intent = Intent(this, Hymn::class.java)
            startActivity(intent)
        }
        members_card.setOnClickListener()
        {
            val intent = Intent(this, LoginActivity::class.java);
            startActivity(intent)
        }

        liturgy_card.setOnClickListener()
        {
            val intent = Intent(this, Liturgy::class.java);
            startActivity(intent)
        }

    }
}