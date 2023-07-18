package com.ack.ststephenskayo

//import android.os.Bundle
//import androidx.activity.compose.setContent
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.tasks.await
//
//class PasswordManager : AppCompatActivity() {
//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            PasswordUpdateScreen()
//        }
//    }
//
//    private suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
//        val querySnapshot = firestore.collection("users")
//            .whereEqualTo("phoneNumber", phoneNumber)
//            .get()
//            .await()
//
//        return if (querySnapshot.isEmpty) {
//            null
//        } else {
//            querySnapshot.documents.first().toObject(User::class.java)
//        }
//    }
//
//    private suspend fun updateUserPassword(user: User, password: String) {
//        user.password = password
//        firestore.collection("users")
//            .document(user.id)
//            .set(user)
//            .await()
//    }
//
//    data class User(val id: String, val name: String, val phoneNumber: String, var password: String)
//
//    @Composable
//    fun PasswordUpdateScreen() {
//        var phoneNumber by remember { mutableStateOf("") }
//        var password by remember { mutableStateOf("") }
//        var confirmPassword by remember { mutableStateOf("") }
//        var showDialog by remember { mutableStateOf(false) }
//        var user: User? by remember { mutableStateOf(null) }
//        var successMessageVisible by remember { mutableStateOf(false) }
//        var errorMessageVisible by remember { mutableStateOf(false) }
//
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            TextField(
//                value = phoneNumber,
//                onValueChange = { phoneNumber = it },
//                label = { Text("Phone Number") }
//            )
//            TextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Password") },
//                visualTransformation = PasswordVisualTransformation()
//            )
//            TextField(
//                value = confirmPassword,
//                onValueChange = { confirmPassword = it },
//                label = { Text("Confirm Password") },
//                visualTransformation = PasswordVisualTransformation()
//            )
//
//            Button(
//                onClick = {
//                    showDialog = true
//                    successMessageVisible = false
//                    errorMessageVisible = false
//                },
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            ) {
//                Text("Change Password")
//            }
//
//            if (showDialog) {
//                LaunchedEffect(phoneNumber) {
//                    user = getUserByPhoneNumber(phoneNumber)
//                }
//
//                AlertDialog(
//                    onDismissRequest = { showDialog = false },
//                    title = { Text("Confirm Password Update") },
//                    text = {
//                        if (user != null) {
//                            Text("Updating password for user: ${user?.name}")
//                        } else {
//                            Text("User with phone number $phoneNumber not found!")
//                        }
//                    },
//                    confirmButton = {
//                        Button(
//                            onClick = {
//                                showDialog = false
//                                if (user != null) {
//                                    // Validate password and confirm password
//                                    if (password == confirmPassword) {
//                                        updateUserPassword(user!!, password)
//                                        successMessageVisible = true
//                                    } else {
//                                        errorMessageVisible = true
//                                    }
//                                } else {
//                                    errorMessageVisible = true
//                                }
//                            }
//                        ) {
//                            Text("Confirm")
//                        }
//                    }
//                )
//            }
//
//            if (successMessageVisible) {
//                Text(
//                    text = "Password updated successfully!",
//                    style = MaterialTheme.typography.body1
//                )
//            }
//
//            if (errorMessageVisible) {
//                Text(
//                    text = "Error: User not found or passwords do not match!",
//                    style = MaterialTheme.typography.body1
//                )
//            }
//        }
//    }
//
//    @Preview(showBackground = true)
//    @Composable
//    fun PreviewPasswordUpdateScreen() {
//        PasswordUpdateScreen()
//    }
//}



import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
                                dialogMessage.value = "User: ${user.name}"
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
                                        dialogMessage.value = "Password updated successfully"
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
        }
    }



class UserRepository() {
    // ...
    val db = FirebaseFirestore.getInstance()

    private val usersCollection = db.collection("users")
        suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
            return suspendCoroutine { continuation ->
                usersCollection
                    .whereEqualTo("phone", phoneNumber)
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
            return suspendCoroutine { continuation ->
                usersCollection
                    .whereEqualTo("phone", phoneNumber)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            continuation.resume(false)
                        } else {
                            val userId = querySnapshot.documents[0].id
                            val user = querySnapshot.documents[0].toObject(User::class.java)
                            user?.password = password

                            usersCollection
                                .document(userId)
                                .set(user!!)
                                .addOnSuccessListener {
                                    continuation.resume(true)
                                }
                                .addOnFailureListener { exception ->
                                    continuation.resumeWithException(exception)
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }
        }


    fun DocumentSnapshot.toUser(): User {
        val id = id
        val fname = getString("firstName") ?: ""
        val sname = getString("lastName") ?:""
        val name = fname + " " + sname
        val phoneNumber = getString("phone") ?: ""
        val password = getString("password")?:""

        return User(id, name, password,phoneNumber)
    }

    // ...
}

data class User(
    val id: String,
    val name: String,
    var password: String,
    val phoneNumber: String
)
