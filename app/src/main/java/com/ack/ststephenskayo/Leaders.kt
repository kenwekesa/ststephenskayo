package com.ack.ststephenskayo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Leaders : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var leaderAdapter: LeaderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setTitle("Kayo leaders")
        setContentView(R.layout.activity_leaders)

        // Initialize the RecyclerView and adapter
        recyclerView = findViewById(R.id.recycler_view_leaders)
        leaderAdapter = LeaderAdapter(emptyList()) // Pass an empty list for now

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Leaders)
            adapter = leaderAdapter
        }

        // Fetch leaders data from Firebase Firestore
        val db = FirebaseFirestore.getInstance()
        val leadersCollection = db.collection("leaders")
        leadersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val leadersList = mutableListOf<LeaderModel>()
                for (document in querySnapshot) {
                    val name = document.getString("name") ?: ""
                    val position = document.getString("position") ?: ""
                    // Add more fields as needed from the document

                    val leader = LeaderModel(name, position)
                    leadersList.add(leader)
                }
                // Update the RecyclerView with the fetched data
                leaderAdapter.leadersList = leadersList
                leaderAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle any error that occurs while fetching data
                Toast.makeText(this, "Error fetching leaders: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
