package com.ack.ststephenskayo



import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog as AD
import androidx.compose.material.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

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

        OutlinedTextField(
            value = paymentData.date.value,
            onValueChange = { paymentData.date.value = it },
            label = { Text("Date") }
        )

        OutlinedTextField(
            value = paymentData.amount.value,
            onValueChange = { paymentData.amount.value = it },
            label = { Text("Amount") }
        )

        Button(
            onClick = {
                // Fetch user name from Firestore
                fetchUserName(paymentData.phoneNumber.value) { userNameValue ->
                    userName.value = userNameValue
                    showDialog.value = true
                }
            } ,
            modifier = Modifier.fillMaxWidth().
            padding(top = 16.dp).
            padding(horizontal = 18.dp)
                .height(12.dp)
        ) {
            Text("Make Payment")
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Confirm Payment") },
                text = { Text("Are you sure you want to make a payment of ${paymentData.amount.value.uppercase()} for Member ~  ${userName.value.uppercase()}?") },
                confirmButton = {
                    Button(onClick = {
                        showDialog.value = false
                        submitPayment(paymentData, context)
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

private fun submitPayment(paymentData: PaymentData, context: Context) {
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
            showPaymentFailureMessage()
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


private fun showPaymentFailureMessage() {
    // Display a failure message, such as a Snackbar or Toast
    // You can customize this based on your UI framework
}
