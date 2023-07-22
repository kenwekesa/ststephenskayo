package com.ack.ststephenskayo




import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PasswordManager: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordManagerScreen()
        }
    }
}






@Composable
fun PasswordManagerScreen() {
    val phoneNumberState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val showSuccessDialog = remember { mutableStateOf(false) }

    val userRepository = UserRepository()

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        OutlinedTextField(
            value = phoneNumberState.value,
            onValueChange = { phoneNumberState.value = it },
            label = { Text("Phone Number") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPasswordState.value,
            onValueChange = { confirmPasswordState.value = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val phoneNumber = phoneNumberState.value
                val password = passwordState.value
                val confirmPassword = confirmPasswordState.value

                if (password == confirmPassword) {
                    scope.launch {
                        val user = userRepository.getUserByPhoneNumber(phoneNumber)
                        if (user != null) {
                            showDialog.value = true
                            dialogMessage.value =
                                "Confirm you want to change the password for ${
                                    (user.firstName + " " + user.lastNamme).uppercase(
                                        Locale.ROOT
                                    )
                                }"
                        } else {
                            showDialog.value = true
                            dialogMessage.value = "User not found"
                        }
                    }
                } else {
                    showDialog.value = true
                    dialogMessage.value = "Password and Confirm Password do not match"
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Change Password")
        }

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

                                val result = userRepository.updateUserPassword(phoneNumber, password)
                                if (result) {
                                    showDialog.value = false
                                    showSuccessDialog.value = true // Show success dialog
                                } else {
                                    showDialog.value = false
                                    dialogMessage.value = "Error updating password"
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
                text = { Text("Password updated successfully!") },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog.value = false
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

//    @Composable
//    fun PasswordManagerScreen() {
//        val phoneNumberState = remember { mutableStateOf("") }
//        val passwordState = remember { mutableStateOf("") }
//        val confirmPasswordState = remember { mutableStateOf("") }
//        val showDialog = remember { mutableStateOf(false) }
//        val dialogMessage = remember { mutableStateOf("") }
//
//       val userRepository = UserRepository()
//
//        val scope = rememberCoroutineScope()
//
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            OutlinedTextField(
//                value = phoneNumberState.value,
//                onValueChange = { phoneNumberState.value = it },
//                label = { Text("Phone Number") }
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = passwordState.value,
//                onValueChange = { passwordState.value = it },
//                label = { Text("Password") },
//                visualTransformation = PasswordVisualTransformation()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = confirmPasswordState.value,
//                onValueChange = { confirmPasswordState.value = it },
//                label = { Text("Confirm Password") },
//                visualTransformation = PasswordVisualTransformation()
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(
//                onClick = {
//                    val phoneNumber = phoneNumberState.value
//                    val password = passwordState.value
//                    val confirmPassword = confirmPasswordState.value
//
//                    if (password == confirmPassword) {
//                        scope.launch {
//                            val user = userRepository.getUserByPhoneNumber(phoneNumber)
//                            if (user != null) {
//                                showDialog.value = true
//                                dialogMessage.value = "Confirm you want to change password for ${
//                                    (user.firstName+" "+user.lastNamme).uppercase(
//                                        Locale.ROOT
//                                    )
//                                }"
//                            } else {
//                                showDialog.value = true
//                                dialogMessage.value = "User not found"
//                            }
//                        }
//                    } else {
//                        showDialog.value = true
//                        dialogMessage.value = "Password and Confirm Password do not match"
//                    }
//                },
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            ) {
//                Text("Change Password")
//            }
//
//            if (showDialog.value) {
//                AlertDialog(
//                    onDismissRequest = { showDialog.value = false },
//                    title = { Text("Alert") },
//                    text = { Text(dialogMessage.value) },
//                    confirmButton = {
//                        Button(
//                            onClick = {
//                                scope.launch {
//                                    val phoneNumber = phoneNumberState.value
//                                    val password = passwordState.value
//
//                                    val result = userRepository.updateUserPassword(phoneNumber, password)
//                                    if (result) {
//                                        showDialog.value = true
//                                        dialogMessage.value = "Password updated successfully"
//                                    } else {
//                                        showDialog.value = false
//                                        dialogMessage.value = "Error updating password"
//                                    }
//                                }
//                            }
//                        ) {
//                            Text("Confirm")
//                        }
//                    },
//                    dismissButton = {
//                        Button(
//                            onClick = { showDialog.value = false }
//                        ) {
//                            Text("Dismiss")
//                        }
//                    }
//                )
//            }
//        }
//    }



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

        suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
            var change_flag: Boolean = false
            usersCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {


                        document.reference.update("password", password)
                            .addOnSuccessListener {
                                // Update successful
                                change_flag = true
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
          return change_flag
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

