package com.ack.ststephenskayo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore

class HymnDetail : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hymn_detail)

        titleTextView = findViewById(R.id.titleTextView)
        contentTextView = findViewById(R.id.contentTextView)

        val hymnNumber = intent.getIntExtra("number", 0)
        val hymnTitle = intent.getStringExtra("title") ?: ""
        val isEnglish = intent.getBooleanExtra("isEnglish", true)

        titleTextView.text = "$hymnNumber. $hymnTitle"

        val firestore = FirebaseFirestore.getInstance()
        val collectionName = if (isEnglish) "Hymns" else "Nyimbo"
        val hymnDocumentRef = firestore.collection(collectionName).document("$hymnNumber")

        hymnDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            val content = documentSnapshot.getString("content") ?: ""
            runOnUiThread {
                contentTextView.text = content
            }
        }.addOnFailureListener { e ->
            // Handle failure, show an error message, log the exception, etc.
        }
    }
}
