package com.ack.ststephenskayo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent


import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

import com.google.firebase.firestore.FirebaseFirestore

class Hymn : AppCompatActivity() {

    private lateinit var switchLanguageButton: Button
    private lateinit var hymnListView: ListView
    private lateinit var hymnAdapter: ArrayAdapter<String>
    private var isEnglish = true // Default language is English

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hymn)

        switchLanguageButton = findViewById(R.id.switchLanguageButton)
        hymnListView = findViewById(R.id.hymnListView)

        hymnAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        hymnListView.adapter = hymnAdapter

        switchLanguageButton.setOnClickListener {
            // Toggle between "English" and "Swahili"
            isEnglish = !isEnglish
            val buttonText = if (isEnglish) "English" else "Swahili"
            switchLanguageButton.text = buttonText

            // Change Firestore collection name accordingly
            val collectionName = if (isEnglish) "Hymns" else "Nyimbo"
            fetchHymnsFromFirestore(collectionName)
        }

        hymnListView.setOnItemClickListener { _, _, position, _ ->
            // Handle item click, open new activity with complete content
            val selectedHymnNumber = position + 1 // Assuming hymn numbers start from 1
            val selectedHymnTitle = hymnAdapter.getItem(position)

            val intent = Intent(this, HymnDetail::class.java)
            intent.putExtra("number", selectedHymnNumber)
            intent.putExtra("title", selectedHymnTitle)
            intent.putExtra("isEnglish", isEnglish)
            startActivity(intent)
        }

        // Initially set the button text to "English" and fetch hymns from "Hymns"
        switchLanguageButton.text = "English"
        fetchHymnsFromFirestore("Hymns")
    }

    private fun fetchHymnsFromFirestore(collectionName: String) {
        val firestore = FirebaseFirestore.getInstance()
        val hymnsCollectionRef = firestore.collection(collectionName)

        hymnsCollectionRef.get().addOnSuccessListener { querySnapshot ->
            val hymnList = mutableListOf<String>()

            for (documentSnapshot in querySnapshot.documents) {
                val number = documentSnapshot.getLong("number") ?: 0
                val title = documentSnapshot.getString("title") ?: ""
                hymnList.add("$number. $title")
            }

            runOnUiThread {
                hymnAdapter.clear()
                hymnAdapter.addAll(hymnList)
            }
        }.addOnFailureListener { e ->
            // Handle failure, show an error message, log the exception, etc.
        }
    }
}
