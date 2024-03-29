package com.ack.ststephenskayo

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class PaymentManager {
    private var totalPayment: Double = 0.0
    private var monthlyPayments: MutableList<MonthlyPayment> = mutableListOf()
   // private var currentDate: Date = Calendar.getInstance

    data class MonthlyPayment(val month: String, var amount: Double, var isCompleted: Boolean)

    @RequiresApi(Build.VERSION_CODES.O)
    fun makePayment(paymentAmount: Double, paymentDate: LocalDate) {
        // Calculate the number of months to distribute the payment
        val currentDate = LocalDate.now()
       // val numberOfMonths = round((paymentDate - currentDate) / 30).toInt()

        val numberOfMonths = Period.between(paymentDate, currentDate).months

        // Divide the payment amount among the months
        val distributedAmount = paymentAmount / numberOfMonths

        // Distribute the amount among the months
        for (i in 1..numberOfMonths) {
            val month = getMonthNameForPayment(i)

            // Check if the payment for this month is already completed
            val existingPayment = monthlyPayments.find { it.month == month }
            if (existingPayment != null && existingPayment.isCompleted) {
                continue
            }

            // Add the distributed amount to the monthly payment
            val payment = existingPayment ?: MonthlyPayment(month, 0.0, false)
            payment.amount += distributedAmount

            // Check if the payment for this month is completed
            if (currentDate.compareTo(paymentDate) >= 0) {
                payment.isCompleted = true
            }

            // Add/update the monthly payment in the list
            if (existingPayment == null) {
                monthlyPayments.add(payment)
            }
        }

        // Update the total payment amount
        totalPayment += paymentAmount
    }

    fun getPaymentStatus(month: String): PaymentStatus {
        val payment = monthlyPayments.find { it.month == month }
        return if (payment != null) {
            if (payment.isCompleted) PaymentStatus.COMPLETED else PaymentStatus.PENDING
        } else {
            PaymentStatus.NOT_FOUND
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getTwentyStatus(phoneNumber: String, callback: (String, Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val dateJoinedString = userDocument.getString("dateJoined")?:LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()

                    // Assuming the dateJoined format is "dd/MM/yyyy"
                    val dateFormatter = DateTimeFormatter.ofPattern("[d/M/yyyy][dd/MM/yyyy]", Locale.getDefault())
                    val dateJoined = LocalDate.parse(dateJoinedString, dateFormatter)

                    val currentDate = LocalDate.now()
                    val weeksSinceJoining = ChronoUnit.WEEKS.between(dateJoined, currentDate)
                    val totalTwentyPaid = userDocument.getLong("total_twenty_paid")?.toInt() ?: 0
                    val twentyOpeningBal: Double = userDocument.getString("openingTwentyBal")?.toDoubleOrNull() ?: 0.0
                    //val expectedTwentyTotalPaid = (weeksSinceJoining * 20).toInt() + userDocument.getLong("twenty_opening_bal")?.toInt()
                    val expectedTwentyTotalPaid = (weeksSinceJoining * 20) +
                            twentyOpeningBal.toInt()


                    val isUpToDate = totalTwentyPaid >= expectedTwentyTotalPaid
                    val message = if (isUpToDate) {
                        "20-20 status: Up to date"
                    } else {
                        val pendingBalance = expectedTwentyTotalPaid - totalTwentyPaid
                        val paymentDueDate = currentDate.minusWeeks(pendingBalance/20L)
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = formatter.format(paymentDueDate)

                        "20-20 status: Not up to date \n\nPending Balance: $pendingBalance /=\n\n" +
                                "Your payments cover up to $formattedDate"
                    }

                    callback(message, isUpToDate)
                } else {
                    callback("User not found", false)
                }
            }
            .addOnFailureListener { e ->
                callback("Error getting document: $e", false)
            }
    }



    //    @RequiresApi(Build.VERSION_CODES.O)
//    fun getTwentyStatus(phoneNumber: String, callback: (String) -> Unit) {
//        val db = FirebaseFirestore.getInstance()
//        val usersCollection = db.collection("users")
//
//        usersCollection
//            .whereEqualTo("phoneNumber", phoneNumber)
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                if (!querySnapshot.isEmpty) {
//                    val userDocument = querySnapshot.documents[0]
//                    val dateJoinedString = userDocument.getString("dateJoined")
//
//                    // Assuming the dateJoined format is "dd/MM/yyyy"
//                    val dateFormatter = DateTimeFormatter.ofPattern("[d/M/yyyy][dd/MM/yyyy]", Locale.getDefault())
//                    val dateJoined = LocalDate.parse(dateJoinedString, dateFormatter)
//
//                    val currentDate = LocalDate.now()
//                    val monthsSinceJoining = ChronoUnit.MONTHS.between(dateJoined, currentDate)
//                    val totalTwentyPaid = userDocument.getLong("total_twenty_paid")?.toInt() ?: 0
//                    val expectedTwentyTotalPaid = (monthsSinceJoining * 100).toInt()
//
//                    val message = if (totalTwentyPaid >= expectedTwentyTotalPaid) {
//                        "20-20 status: Up to date"
//                    } else {
//                        "20-20 status: Not up to date \nPending Balance:"+(expectedTwentyTotalPaid-totalTwentyPaid).toString()+"/=\n" +
//                                "Your payments are up to "+currentDate.minusMonths((expectedTwentyTotalPaid-totalTwentyPaid)/100L)
//                    }
//
//                    callback(message)
//                } else {
//                    callback("User not found")
//                }
//            }
//            .addOnFailureListener { e ->
//                callback("Error getting document: $e")
//            }
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getWelfareStatus(phoneNumber: String, callback: (String, Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val dateJoinedString = userDocument.getString("dateJoined")?:LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()

                    // Assuming the dateJoined format is "dd/MM/yyyy"
                    val dateFormatter = DateTimeFormatter.ofPattern("[d/M/yyyy][dd/MM/yyyy]", Locale.getDefault())
                    val dateJoined = LocalDate.parse(dateJoinedString, dateFormatter)

                    val currentDate = LocalDate.now()
                    val monthsSinceJoining = ChronoUnit.MONTHS.between(dateJoined, currentDate)
                    val totalWelfarePaid = userDocument.getLong("total_welfare_paid")?.toInt() ?: 0
                    val welfareOpeningBal: Double = userDocument.getString("openingWelfareBal")?.toDoubleOrNull() ?: 0.0

                    val expectedTotalPaid = (monthsSinceJoining * 100).toInt() + welfareOpeningBal.toInt()

                    val isUpToDate = totalWelfarePaid >= expectedTotalPaid
                    val message = if (isUpToDate) {
                        "Welfare payment status: Up to date"
                    } else {
                        val pendingBalance = expectedTotalPaid - totalWelfarePaid
                        val paymentDueDate = currentDate.minusMonths(pendingBalance / 100L)
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = formatter.format(paymentDueDate)

                        "Welfare payment status: Not up to date \n\nPending Balance: $pendingBalance /=\n\n" +
                                "Your payments cover up to $formattedDate"
                    }

                    callback(message, isUpToDate)
                } else {
                    callback("User not found", false)
                }
            }
            .addOnFailureListener { e ->
                callback("Error getting document: $e", false)
            }
    }


    fun getTotalPaymentAmount(): Double {
        return totalPayment
    }

    private fun getMonthNameForPayment(monthOffset: Int): String {
        // Logic to calculate the month name based on the current date and monthOffset
        return TODO("Provide the return value")
    }
}

enum class PaymentStatus(s: String) {
    COMPLETED("Up to date, you can benefit from welfare"),
    PENDING("Payment pending"),
    NOT_FOUND("Not up to date, you cannot benefit from welfare")
}
