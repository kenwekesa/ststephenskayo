package com.ack.ststephenskayo



import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog as AD
import androidx.compose.material.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Calendar

class Payment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setTitle("Record Payment")
        setContent {
            PaymentForm(context = this)
        }
    }
}

data class PaymentData(
    val phoneNumber: MutableState<String> = mutableStateOf(""),
    val date: MutableState<String> = mutableStateOf(""),
    val amount: MutableState<String> = mutableStateOf("")
)

@Composable
fun PaymentForm(context: Context) {
    val paymentData = remember { PaymentData() }
    val showDialog = remember { mutableStateOf(false) }
    val userName = remember { mutableStateOf("") }

    val userExists = remember { mutableStateOf(false) }
    val loading = remember { mutableStateOf(false) }


    val formattedDate = remember { mutableStateOf("") }



    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val context = LocalContext.current

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    var buttonText = "Date Joined"

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            formattedDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
    )


    Column(
        Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = paymentData.phoneNumber.value,
            onValueChange = { paymentData.phoneNumber.value = it },
            label = { Text("Phone Number") }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Button(
                onClick = { mDatePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
            ) {
                Text(text = "Date", color = Color.White)
            }
            Text(
                text = formattedDate.value,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 8.dp)
            )
        }


        OutlinedTextField(
            value = paymentData.amount.value,
            onValueChange = { paymentData.amount.value = it },
            label = { Text("Amount") }
        )

        paymentData.date.value = formattedDate.value;
        Button(
            onClick = {
                // Fetch user name from Firestore
                // Fetch user name from Firestore
                fetchUserName(paymentData.phoneNumber.value) { userNameValue ->
                    if (userNameValue.isNotEmpty()) {
                        userExists.value = true
                        userName.value = userNameValue
                        showDialog.value = true
                    } else {
                        userExists.value = false
                        showNoUserMessage(context)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
                .padding(top = 6.dp)
                .padding(horizontal = 18.dp)
                .height(48.dp)
        ) {
            Text("Make Payment")
        }


        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Confirm Payment") },
                text = { Text("Confirm you want to make payment of ${paymentData.amount.value.uppercase()} for Member ~  ${userName.value.uppercase()}?") },
                confirmButton = {
                    Button( onClick = {
                        showDialog.value = false
                        loading.value = true
                        updateTotalWelfare(paymentData.phoneNumber.value, paymentData.amount.value.toInt())
                        submitPayment(paymentData, context) { success ->

                            if (success) {
                                loading.value = false
                                showPaymentSuccessMessage(context)
                            } else {
                                loading.value = false
                                showPaymentFailureMessage(context)
                            }
                        }
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog.value = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (loading.value) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    }
}

private fun fetchUserName(phoneNumber: String, onUserNameFetched: (String) -> Unit) {
    // Replace "users" with your Firestore collection name for user data
    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .whereEqualTo("phoneNumber", phoneNumber)
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {

                val userDocument = querySnapshot.documents.first()

                val userName = userDocument.getString("firstName") +" "+ userDocument.getString("lastName")?: ""
                onUserNameFetched(userName)
            } else {
                onUserNameFetched("")
            }
        }
        .addOnFailureListener { exception ->
            // Handle the failure case
            onUserNameFetched("")
        }
}


private fun updateTotalWelfare(phoneNumber: String, amountPaid: Int) {
    val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("users")

    usersCollection
        .whereEqualTo("phoneNumber", phoneNumber)
        .get()
        .addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot.documents) {
                val currentTotalPaid = document.getLong("total_welfare_paid") ?: 0
                val newTotalPaid = currentTotalPaid + amountPaid

                document.reference.update("total_welfare_paid", newTotalPaid)
                    .addOnSuccessListener {
                        // Update successful
                        println("Total welfare paid updated successfully")
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors
                        println("Error updating total welfare paid: $e")
                    }
            }
        }
        .addOnFailureListener { e ->
            // Handle any errors
            println("Error getting documents: $e")
        }
}

private fun submitPayment(paymentData: PaymentData, context: Context, onComplete: (Boolean) -> Unit) {
    // Replace "payments" with your Firestore collection name for payments
    val db = FirebaseFirestore.getInstance()
    val payment = hashMapOf(
        "phoneNumber" to paymentData.phoneNumber.value,
        "date" to paymentData.date.value,
        "amount" to paymentData.amount.value
    )
    db.collection("payments")
        .add(payment)
        .addOnSuccessListener {
            // Payment submitted successfully
            // Show success message or perform additional actions
            // You can also navigate to another screen if needed
            showPaymentSuccessMessage(context)
        }
        .addOnFailureListener { exception ->
            // Handle the failure case
            showPaymentFailureMessage(context)
        }
}
fun showPaymentSuccessMessage(context: Context) {
    val alertDialog = AD.Builder(context)
        .setTitle("Payment Success")
        .setMessage("Your has been recorded successfully.")
        .setPositiveButton("OK") { _, _ ->
            // Perform additional actions or close the dialog if needed
        }
        .create()

    alertDialog.show()
}




private fun showNoUserMessage(context: Context) {
    val alertDialog = AD.Builder(context)
        .setTitle("User Not Found")
        .setMessage("No user found with the provided phone number.")
        .setPositiveButton("OK") { _, _ ->
            // Perform additional actions or close the dialog if needed
        }
        .create()

    alertDialog.show()
}



private fun showPaymentFailureMessage(context: Context) {
    val alertDialog = AD.Builder(context)
        .setTitle("Payment Failure")
        .setMessage("Failed to record your payment. Please try again later.")
        .setPositiveButton("OK") { _, _ ->
            // Perform additional actions or close the dialog if needed
        }
        .create()

    alertDialog.show()
}
