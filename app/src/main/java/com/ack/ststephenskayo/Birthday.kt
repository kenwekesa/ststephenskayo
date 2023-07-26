package com.ack.ststephenskayo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class Birthday : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_birthday)

        super.setTitle("Birthday babies")
        // Step 1: Initialize Firebase
        val firestore = FirebaseFirestore.getInstance()

        // Step 2: Get the current date in the format of your birthDate and birthMonth (2 and Jul)
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
        val todayDate = dateFormat.format(currentDate.time)

        // Step 3: Query the Firestore collection to get users with matching birthDate and birthMonth
        firestore.collection("users")
            .whereEqualTo("birthDate", todayDate.substringBefore(" "))
            .whereEqualTo("birthMonth", todayDate.substringAfter(" "))
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Step 4: Process the query results and display in a RecyclerView
                val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                val userAdapter = UserAdapter()
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = userAdapter

                val userList = mutableListOf<Birthdayuser>()

                for (document in querySnapshot.documents) {
                    val fname = document.getString("firstName") ?: ""
                    val mname = document.getString("middleName") ?: ""
                    val lname = document.getString("lastName") ?: ""

                    val phoneNumber = document.getString("phoneNumber") ?: ""
                    userList.add(Birthdayuser(fname,mname,lname, phoneNumber))
                }

                userAdapter.setUserList(userList)

                // Handle the "Share Wishes" button click event
                val btnShareWishes: Button = findViewById(R.id.btnShareWishes)
                btnShareWishes.setOnClickListener {
                    // Create the message to share
                    val message = buildMessageToShare(userList)

                    // Share the message on WhatsApp
                    shareOnWhatsApp(message)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that may occur during the query
                println("Error getting documents: $exception")
            }


    }
    private fun buildMessageToShare(userList: List<Birthdayuser>): String {


        val stringBuilder = StringBuilder()

        stringBuilder.append("🎉🎂 *Happy Birthday, Dear Brothers and Sisters!* 🎂🎉\n\n")
        stringBuilder.append("Today, we want to celebrate the birthdays of some amazing members in our midst. Let us take a moment to wish them all a year filled with God's blessings, love, and grace! 🙏❤️\n\n")

        stringBuilder.append("🎉 *Birthday Celebrants:* 🎉\n\n")

        for ((index, usr) in userList.withIndex()) {
            var name:String = usr.firstName+" "+usr.middleName+" "+usr.lastName
            stringBuilder.append("*${index + 1}. $name* 🎈\n")

        }

        stringBuilder.append("\nLet's join hands and pray for these brothers and sisters, asking God to guide them in His love throughout the coming year. 🙏❤️\n\n")
        stringBuilder.append("Happy Birthday once again! May your lives continue to be a testimony of God's grace and His unconditional love for each one of us. 🙏❤️🕊️\n\n")
        stringBuilder.append("With love and blessings,\nST. STEPHENS KAYO FAMILY") // Replace [Your Name] with your actual name



        return stringBuilder.toString()
    }

    private fun shareOnWhatsApp(message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.setPackage("com.whatsapp")
        startActivity(intent)
    }
}

class UserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userList: List<Birthdayuser> = emptyList()
    private val rowBackgroundColors = mutableListOf<Int>()


    fun setUserList(userList: List<Birthdayuser>) {
        this.userList = userList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.birthday_row_user_data, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.birthday_row_user_data, parent, false)
            UserViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserViewHolder) {
            val user = userList[position - 1]
            holder.bind(user)

            // Set text color and style for data rows (non-header rows)
            holder.textName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.dataRowTextColor))
            holder.textPhoneNumber.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.dataRowTextColor))
            holder.textName.setTypeface(null, Typeface.NORMAL) // Set normal text style
            holder.textPhoneNumber.setTypeface(null, Typeface.NORMAL) // Set normal text style

            // Set background color for data rows
            val bgColor = rowBackgroundColors.getOrNull(position - 1)
            if (bgColor != null) {
                holder.itemView.setBackgroundColor(bgColor)
            } else {
                // Set a default background color for data rows (if needed)
                val defaultBgColor = ContextCompat.getColor(holder.itemView.context, R.color.defaultRowBackgroundColor)
                holder.itemView.setBackgroundColor(defaultBgColor)
            }
        } else if (holder is HeaderViewHolder) {
            // Set the table headings
            holder.textNameHeading.text = "Name"
            holder.textPhoneNumberHeading.text = "Phone Number"

            // Set text color and style for header row
            holder.textNameHeading.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.headerRowTextColor))
            holder.textPhoneNumberHeading.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.headerRowTextColor))
            holder.textNameHeading.setTypeface(null, Typeface.BOLD) // Set bold text style
            holder.textPhoneNumberHeading.setTypeface(null, Typeface.BOLD) // Set bold text style

            // Set background color for header row (if needed)
            val headerBgColor = ContextCompat.getColor(holder.itemView.context, R.color.headerRowBackgroundColor)
            holder.itemView.setBackgroundColor(headerBgColor)
        }
    }

    // Function to set row background colors from outside the adapter
    fun setRowBackgroundColors(colors: List<Int>) {
        rowBackgroundColors.clear()
        rowBackgroundColors.addAll(colors)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return userList.size + 1 // Add 1 for the header row
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textNameHeading)
        val textPhoneNumber: TextView = itemView.findViewById(R.id.textPhoneNumberHeading)

        @SuppressLint("SetTextI18n")
        fun bind(user: Birthdayuser) {
            textName.text = user.firstName+" "+user.middleName+" "+user.lastName
            textPhoneNumber.text = user.phoneNumber
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textNameHeading: TextView = itemView.findViewById(R.id.textNameHeading)
        val textPhoneNumberHeading: TextView = itemView.findViewById(R.id.textPhoneNumberHeading)
    }
}



// User.kt
data class Birthdayuser(val firstName: String,val middleName: String, val lastName: String, val phoneNumber: String)
