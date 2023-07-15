package com.ack.ststephenskayo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AllMembers : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val membersCollection = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_members)

        membersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val membersList = mutableListOf<Member>()

                for (document in querySnapshot) {
                    val id = document.id
                    val fname = document.getString("firstname") ?: ""
                    val sname = document.getString("lastname") ?: ""

                    val name = fname + " " + sname;
                    val dateJoined = document.getString("dateJoined") ?: ""
                    val totalPaid = document.getDouble("total_welfare_paid") ?: 0.0

                    val member = Member(id, name, dateJoined, totalPaid)
                    membersList.add(member)
                }

                val recyclerView: RecyclerView = findViewById(R.id.members_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(this)
                val adapter = MembersAdapter(membersList)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during fetching data
            }
    }
}
