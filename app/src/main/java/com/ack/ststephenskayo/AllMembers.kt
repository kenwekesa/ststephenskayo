package com.ack.ststephenskayo
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    lateinit var sharedPrefs:SharedPreferences
    lateinit var usertype:String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_members)
        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved values from SharedPreferences
        usertype = sharedPrefs.getString("usertype", null) as String

        membersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val membersList = mutableListOf<Member>()

                for (document in querySnapshot) {
                    val id = document.id
                    val phoneNumber = document.getString("phoneNumber")?:""
                    val fellowship = document.getString("fellowship")?:"No fellowship yet"
                    val fname = document.getString("firstName") ?: ""
                    val sname = document.getString("lastName") ?: ""

                    val name = fname + " " + sname;
                    val dateJoined = document.getString("dateJoined") ?: ""
                    val totalPaid = when (usertype) {
                        "welfare_admin" -> document.getDouble("total_welfare_paid") ?: 0.0
                        "twenty_admin" -> document.getDouble("total_twenty_paid") ?: 0.0
                        else -> 0.0
                    }
                    val totalWelfarePaid: Double = document.getDouble("total_welfare_paid")?:0.0;
                    val totalTwentyPaid: Double = document.getDouble("total_twenty_paid")?:0.0
                    //val totalTwentyOpeningBal: Double = document.getDouble("openingTwentyBal")?:0.0
                    val totalTwentyOpeningBal: Double = document.getString("openingTwentyBal")?.toDoubleOrNull() ?: 0.0
                    val totalWelfareOpeningBal: Double = document.getString("openingWelfareBal")?.toDoubleOrNull() ?: 0.0

                    //val totalWelfareOpeningBal: Double = document.getDouble("openingWelfareBal")?:0.0


                    val dateFormatter = DateTimeFormatter.ofPattern("[d/M/yyyy][dd/MM/yyyy]", Locale.getDefault())

                    val balance = if (dateJoined?.isNotEmpty() == true) {
                        when (usertype) {
                            "welfare_admin" -> ((ChronoUnit.MONTHS.between(LocalDate.parse(dateJoined, dateFormatter), LocalDate.now()) * 100).toDouble() - totalWelfarePaid) + totalWelfareOpeningBal
                            "twenty_admin" -> ((ChronoUnit.WEEKS.between(LocalDate.parse(dateJoined, dateFormatter), LocalDate.now()) * 20).toDouble() - totalTwentyPaid) + totalTwentyOpeningBal
                            else -> 0.0
                        }

                    } else {
                        0.0
                    }

                    //val balance = (ChronoUnit.MONTHS.between(LocalDate.parse(dateJoined, dateFormatter), LocalDate.now()) * 100).toInt() - totalPaid;

                    val member = Member(id, name,phoneNumber,fellowship, dateJoined, totalPaid, balance)
                    membersList.add(member)
                }

                val recyclerView: RecyclerView = findViewById(R.id.members_recycler_view)
                recyclerView.layoutManager = LinearLayoutManager(this)
                val adapter = MembersAdapter(membersList)
                recyclerView.adapter = adapter

                // Set click listener for the RecyclerView items
                adapter.setOnItemClickListener(object : MembersAdapter.OnItemClickListener {
                    override fun onItemClick(member: Member) {
                        // Handle the item click here
                        val intent = Intent(this@AllMembers, ViewMember::class.java)
                        intent.putExtra("phoneNumber", member.phoneNumber)
                        intent.putExtra("memberName", member.name)
                        intent.putExtra("memberDateJoined", member.dateJoined)
                        intent.putExtra("memberTotalPaid", member.totalPaid)
                        intent.putExtra("fellowship", member.fellowship)
                        startActivity(intent)
                    }
                })
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during fetching data
            }
    }
}
