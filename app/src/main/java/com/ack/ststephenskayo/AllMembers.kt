package com.ack.ststephenskayo
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class AllMembers : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val membersCollection = db.collection("users")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_members)

        membersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val membersList = mutableListOf<Member>()

                for (document in querySnapshot) {
                    val id = document.id
                    val fname = document.getString("firstName") ?: ""
                    val sname = document.getString("lastName") ?: ""

                    val name = fname + " " + sname;
                    val dateJoined = document.getString("dateJoined") ?: ""
                    val totalPaid = document.getDouble("total_welfare_paid") ?: 0.0

                    val dateFormatter = DateTimeFormatter.ofPattern("[d/M/yyyy][dd/MM/yyyy]", Locale.getDefault())

                    val balance = (ChronoUnit.MONTHS.between(LocalDate.parse(dateJoined, dateFormatter), LocalDate.now()) * 100).toInt() - totalPaid;

                    val member = Member(id, name, dateJoined, totalPaid, balance)
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
