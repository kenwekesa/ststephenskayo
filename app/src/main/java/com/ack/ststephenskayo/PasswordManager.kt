package com.ack.ststephenskayo




import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PasswordManager: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordManagerScreen(context = this@PasswordManager)
        }
    }
}






@Composable
fun PasswordManagerScreen(context: Context) {
    val phoneNumberState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val showSuccessDialog = remember { mutableStateOf(false) }

    val userRepository = UserRepository()

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = phoneNumberState.value,
                onValueChange = { phoneNumberState.value = it },
                label = { Text("Phone Number") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ... other UI elements ...

            Button(
                onClick = {
                    val phoneNumber = phoneNumberState.value
                    val password = passwordState.value
                    val confirmPassword = confirmPasswordState.value

                    scope.launch {
                        val user = userRepository.getUserByPhoneNumber(phoneNumber)
                        if (user != null) {
                            showDialog.value = true
                            dialogMessage.value =
                                "Confirm you want to reset account for ${
                                    (user.firstName + " " + user.lastNamme).uppercase(
                                        Locale.ROOT
                                    )
                                }"
                        } else {
                            showNoUserMessage(context)
                        }
                    }
                }
            ) {
                Text("Reset Account")
            }

            // ... other UI elements ...

            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { Text("Alert") },
                    text = { Text(dialogMessage.value) },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    val phoneNumber = phoneNumberState.value
                                    val password = passwordState.value

                                    val result = userRepository.updateUserPassword(
                                        phoneNumber,
                                        password
                                    )
                                    if (result) {
                                        showresetSuccessMessage(context)
                                    } else {
                                        showresetFailureMessage(context)
                                    }
                                }
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog.value = false }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )
            }

            if (showSuccessDialog.value) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog.value = false },
                    title = { Text("Success") },
                    text = { Text("Account successfully reset!") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog.value = false
                                if (context is Activity) {
                                    context.finish()
                                }
                                // Close the activity when the user clicks "OK"
                                // You can replace this with the appropriate code to close the activity
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}



fun showresetSuccessMessage(context: Context) {
    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
        .setTitle("Reset Success")
        .setMessage("The user account successfully reset. Login with phone number as your new password")
        .setPositiveButton("OK") { _, _ ->
            // Perform additional actions or close the dialog if needed

            if (context is Activity) {
                context.finish()
            }
        }
        .create()

    alertDialog.show()
}




private fun showNoUserMessage(context: Context) {
    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
        .setTitle("User Not Found")
        .setMessage("No user found with the provided phone number.")
        .setPositiveButton("OK") { _, _ ->
            // Perform additional actions or close the dialog if needed
        }
        .create()

    alertDialog.show()
}



private fun showresetFailureMessage(context: Context) {
    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
        .setTitle("Error reseting")
        .setMessage("An error occured while resetting, try again later.")
        .setPositiveButton("OK") { _, _ ->
            // Perform additional actions or close the dialog if needed
        }
        .create()

    alertDialog.show()
}



class UserRepository() {
    // ...
    val db = FirebaseFirestore.getInstance()

    private val usersCollection = db.collection("users")
        suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
            return suspendCoroutine { continuation ->
                usersCollection
                    .whereEqualTo("phoneNumber", phoneNumber)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            continuation.resume(null)
                        } else {
                            val user = querySnapshot.documents[0].toUser()
                            continuation.resume(user)
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
        }

//        suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
//            var change_flag: Boolean = false
//
//            val updates = hashMapOf<String, Any>(
//                "password" to phoneNumber,
//                "user_activated" to false
//            )
//            usersCollection
//                .whereEqualTo("phoneNumber", phoneNumber)
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//                    for (document in querySnapshot.documents) {
//                        document.reference.update(updates)
//                            .addOnSuccessListener {
//                                // Update successful
//                                change_flag = true
//                            }
//                            .addOnFailureListener { e ->
//                                // Handle any errors
//                                println("Error resetting account $e")
//                            }
//                    }
//                }
//                .addOnFailureListener { e ->
//                    // Handle any errors
//                    println("Error getting documents: $e")
//                }
//          return change_flag
//        }

    fun Any?.toAny(): Any? {
        return when (this) {
            is String,
            is Number,
            is Boolean -> this
            is Map<*, *> -> this.mapValues { it.value.toAny() }
            is List<*> -> this.map { it.toAny() }
            is DocumentReference -> this
            is FieldValue -> this
            else -> null
        }
    }
    suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
        try {
            val updates = hashMapOf(
                "password" to phoneNumber.toAny(),
                "user_activated" to false.toAny()
            )

            val querySnapshot = usersCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .await() // Use await() to suspend and wait for the result

            for (document in querySnapshot.documents) {
                document.reference.update(updates).await() // Suspend and wait for the update to complete
            }

            return true // If the loop completes without any exceptions, the updates were successful
        } catch (e: FirebaseFirestoreException) {
            // Handle any errors
            println("Error updating user password: $e")
            return false
        }
    }

    fun DocumentSnapshot.toUser(): User {
        val fname = getString("firstName") ?: ""
        val sname = getString("lastName") ?:""





        return User(fname,sname)
    }

    // ...
}

data class User(
    val firstName: String,
    val lastNamme:String,



)

