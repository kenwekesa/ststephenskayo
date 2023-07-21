//package com.ack.ststephenskayo;
//
////import android.os.Bundle
////import androidx.activity.compose.setContent
////import androidx.appcompat.app.AppCompatActivity
////import androidx.compose.foundation.layout.*
////import androidx.compose.material.*
////import androidx.compose.runtime.*
////import androidx.compose.ui.Alignment
////import androidx.compose.ui.Modifier
////import androidx.compose.ui.text.input.PasswordVisualTransformation
////import androidx.compose.ui.tooling.preview.Preview
////import androidx.compose.ui.unit.dp
////import com.google.firebase.firestore.FirebaseFirestore
////import kotlinx.coroutines.tasks.await
////
////class PasswordManager : AppCompatActivity() {
////    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
////
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        setContent {
////            PasswordUpdateScreen()
////        }
////    }
////
////    private suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
////        val querySnapshot = firestore.collection("users")
////            .whereEqualTo("phoneNumber", phoneNumber)
////            .get()
////            .await()
////
////        return if (querySnapshot.isEmpty) {
////            null
////        } else {
////            querySnapshot.documents.first().toObject(User::class.java)
////        }
////    }
////
////    private suspend fun updateUserPassword(user: User, password: String) {
////        user.password = password
////        firestore.collection("users")
////            .document(user.id)
////            .set(user)
////            .await()
////    }
////
////    data class User(val id: String, val name: String, val phoneNumber: String, var password: String)
////
////    @Composable
////    fun PasswordUpdateScreen() {
////        var phoneNumber by remember { mutableStateOf("") }
////        var password by remember { mutableStateOf("") }
////        var confirmPassword by remember { mutableStateOf("") }
////        var showDialog by remember { mutableStateOf(false) }
////        var user: User? by remember { mutableStateOf(null) }
////        var successMessageVisible by remember { mutableStateOf(false) }
////        var errorMessageVisible by remember { mutableStateOf(false) }
////
////        Column(
////            modifier = Modifier.padding(16.dp),
////            verticalArrangement = Arrangement.spacedBy(16.dp)
////        ) {
////            TextField(
////                value = phoneNumber,
////                onValueChange = { phoneNumber = it },
////                label = { Text("Phone Number") }
////            )
////            TextField(
////                value = password,
////                onValueChange = { password = it },
////                label = { Text("Password") },
////                visualTransformation = PasswordVisualTransformation()
////            )
////            TextField(
////                value = confirmPassword,
////                onValueChange = { confirmPassword = it },
////                label = { Text("Confirm Password") },
////                visualTransformation = PasswordVisualTransformation()
////            )
////
////            Button(
////                onClick = {
////                    showDialog = true
////                    successMessageVisible = false
////                    errorMessageVisible = false
////                },
////                modifier = Modifier.align(Alignment.CenterHorizontally)
////            ) {
////                Text("Change Password")
////            }
////
////            if (showDialog) {
////                LaunchedEffect(phoneNumber) {
////                    user = getUserByPhoneNumber(phoneNumber)
////                }
////
////                AlertDialog(
////                    onDismissRequest = { showDialog = false },
////                    title = { Text("Confirm Password Update") },
////                    text = {
////                        if (user != null) {
////                            Text("Updating password for user: ${user?.name}")
////                        } else {
////                            Text("User with phone number $phoneNumber not found!")
////                        }
////                    },
////                    confirmButton = {
////                        Button(
////                            onClick = {
////                                showDialog = false
////                                if (user != null) {
////                                    // Validate password and confirm password
////                                    if (password == confirmPassword) {
////                                        updateUserPassword(user!!, password)
////                                        successMessageVisible = true
////                                    } else {
////                                        errorMessageVisible = true
////                                    }
////                                } else {
////                                    errorMessageVisible = true
////                                }
////                            }
////                        ) {
////                            Text("Confirm")
////                        }
////                    }
////                )
////            }
////
////            if (successMessageVisible) {
////                Text(
////                    text = "Password updated successfully!",
////                    style = MaterialTheme.typography.body1
////                )
////            }
////
////            if (errorMessageVisible) {
////                Text(
////                    text = "Error: User not found or passwords do not match!",
////                    style = MaterialTheme.typography.body1
////                )
////            }
////        }
////    }
////
////    @Preview(showBackground = true)
////    @Composable
////    fun PreviewPasswordUpdateScreen() {
////        PasswordUpdateScreen()
////    }
////}
//
//
//
//import android.annotation.SuppressLint;
//import android.app.DatePickerDialog
//import android.content.Context
//import android.os.Bundle
//import android.widget.DatePicker
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.AlertDialog
//import androidx.compose.material.Button
//import androidx.compose.material.ButtonDefaults
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.OutlinedTextField
//import androidx.compose.material.Text
//import androidx.compose.material.TextField
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusState
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.toUpperCase
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.google.firebase.firestore.DocumentSnapshot
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import kotlinx.coroutines.withContext
//import java.util.Calendar
//import java.util.Locale
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//import kotlin.coroutines.suspendCoroutine
//
//
//class SetPassword: ComponentActivity() {
//        override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//        PasswordManagerScreen()
//        }
//        }
//        }
//
//
//
//
////@Composable
////fun PaymentForm(context: Context) {
////    val paymentData = remember { PaymentData() }
////    val showDialog = remember { mutableStateOf(false) }
////    val userName = remember { mutableStateOf("") }
////
////    val userExists = remember { mutableStateOf(false) }
////    val loading = remember { mutableStateOf(false) }
////
////
////    val formattedDate = remember { mutableStateOf("") }
////
////
////
////    val mYear: Int
////    val mMonth: Int
////    val mDay: Int
////    val context = LocalContext.current
////
////    // Initializing a Calendar
////    val mCalendar = Calendar.getInstance()
////
////    // Fetching current year, month and day
////    mYear = mCalendar.get(Calendar.YEAR)
////    mMonth = mCalendar.get(Calendar.MONTH)
////    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
////
////    var buttonText = "Date Joined"
////
////    val mDatePickerDialog = DatePickerDialog(
////        context,
////        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
////            formattedDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
////        }, mYear, mMonth, mDay
////    )
////
////
////    Column(
////        Modifier.fillMaxWidth().padding(top = 64.dp),
////        horizontalAlignment = Alignment.CenterHorizontally,
////        verticalArrangement = Arrangement.Center
////    ) {
////        OutlinedTextField(
////            value = paymentData.phoneNumber.value,
////            onValueChange = { paymentData.phoneNumber.value = it },
////            label = { Text("Phone Number") }
////        )
////
////        Row(
////            verticalAlignment = Alignment.CenterVertically,
////            modifier = Modifier.padding(vertical = 8.dp)
////        ) {
////            Button(
////                onClick = { mDatePickerDialog.show() },
////                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
////            ) {
////                Text(text = "Date", color = Color.White)
////            }
////            Text(
////                text = formattedDate.value,
////                fontSize = 14.sp,
////                textAlign = TextAlign.Center,
////                modifier = Modifier.padding(start = 8.dp)
////            )
////        }
////
////
////        OutlinedTextField(
////            value = paymentData.amount.value,
////            onValueChange = { paymentData.amount.value = it },
////            label = { Text("Amount") }
////        )
////
////        paymentData.date.value = formattedDate.value;
////        Button(
////            onClick = {
////                // Fetch user name from Firestore
////                // Fetch user name from Firestore
////                fetchUserName(paymentData.phoneNumber.value) { userNameValue ->
////                    if (userNameValue.isNotEmpty()) {
////                        userExists.value = true
////                        userName.value = userNameValue
////                        showDialog.value = true
////                    } else {
////                        userExists.value = false
////                        showNoUserMessage(context)
////                    }
////                }
////            },
////            modifier = Modifier.fillMaxWidth()
////                .padding(top = 6.dp)
////                .padding(horizontal = 18.dp)
////                .height(48.dp)
////        ) {
////            Text("Make Payment")
////        }
////
////
////        if (showDialog.value) {
////            AlertDialog(
////                onDismissRequest = { showDialog.value = false },
////                title = { Text("Confirm Payment") },
////                text = { Text("Confirm you want to change the password for Member ~  ${userName.value.uppercase()}?") },
////                confirmButton = {
////                    Button( onClick = {
////                        showDialog.value = false
////                        loading.value = true
////                        updatePassword(paymentData.phoneNumber.value, paymentData.amount.value.toInt())
//////                        submitPayment(paymentData, context) { success ->
//////
//////                            if (success) {
//////                                loading.value = false
//////                                showPaymentSuccessMessage(context)
//////                            } else {
//////                                loading.value = false
//////                                showPaymentFailureMessage(context)
//////                            }
//////                        }
////                    }) {
////                        Text("Confirm")
////                    }
////                },
////                dismissButton = {
////                    Button(onClick = { showDialog.value = false }) {
////                        Text("Cancel")
////                    }
////                }
////            )
////        }
////
////        if (loading.value) {
////            CircularProgressIndicator(modifier = Modifier.size(48.dp))
////        }
////    }
////}
////
////
////
////private fun fetchUserName(phoneNumber: String, onUserNameFetched: (String) -> Unit) {
////    // Replace "users" with your Firestore collection name for user data
////    val db = FirebaseFirestore.getInstance()
////    db.collection("users")
////        .whereEqualTo("phoneNumber", phoneNumber)
////        .get()
////        .addOnSuccessListener { querySnapshot ->
////            if (!querySnapshot.isEmpty) {
////
////                val userDocument = querySnapshot.documents.first()
////
////                val userName = userDocument.getString("firstName") +" "+ userDocument.getString("lastName")?: ""
////                onUserNameFetched(userName)
////            } else {
////                onUserNameFetched("")
////            }
////        }
////        .addOnFailureListener { exception ->
////            // Handle the failure case
////            onUserNameFetched("")
////        }
////}
////
////
////
////private fun updatePassword(phoneNumber: String, password: String) {
////    val db = FirebaseFirestore.getInstance()
////    val usersCollection = db.collection("users")
////
////    usersCollection
////        .whereEqualTo("phoneNumber", phoneNumber)
////        .get()
////        .addOnSuccessListener { querySnapshot ->
////            for (document in querySnapshot.documents) {
////
////
////                document.reference.update("password", password)
////                    .addOnSuccessListener {
////                        // Update successful
////                        println("Password Changed successfully")
////                    }
////                    .addOnFailureListener { e ->
////                        // Handle any errors
////                        println("Error changing the password: $e")
////                    }
////            }
////        }
////        .addOnFailureListener { e ->
////            // Handle any errors
////            println("Error getting documents: $e")
////        }
////}
////
////
////fun showPasswordChangeSuccessMessage(context: Context) {
////    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
////        .setTitle("Payment Success")
////        .setMessage("Your has been recorded successfully.")
////        .setPositiveButton("OK") { _, _ ->
////            // Perform additional actions or close the dialog if needed
////        }
////        .create()
////
////    alertDialog.show()
////}
////
////
////
////
////private fun showNoUserMessage(context: Context) {
////    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
////        .setTitle("User Not Found")
////        .setMessage("No user found with the provided phone number.")
////        .setPositiveButton("OK") { _, _ ->
////            // Perform additional actions or close the dialog if needed
////        }
////        .create()
////
////    alertDialog.show()
////}
////
////
////
////private fun showPaymentFailureMessage(context: Context) {
////    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
////        .setTitle("Password Change failure")
////        .setMessage("Failed to change the password, try again.")
////        .setPositiveButton("OK") { _, _ ->
////            // Perform additional actions or close the dialog if needed
////        }
////        .create()
////
////    alertDialog.show()
////}
//////---------------------------------------------------------------------------
//
//
//
//@Composable
//    fun PasswordManagerScreen() {
//            val phoneNumberState = remember { mutableStateOf("") }
//            val passwordState = remember { mutableStateOf("") }
//            val confirmPasswordState = remember { mutableStateOf("") }
//            val showDialog = remember { mutableStateOf(false) }
//            val dialogMessage = remember { mutableStateOf("") }
//
//            val userRepository = UserRepository()
//
//            val scope = rememberCoroutineScope()
//
//            Column(
//            modifier = Modifier.padding(16.dp)
//            ) {
//            OutlinedTextField(
//            value = phoneNumberState.value,
//            onValueChange = { phoneNumberState.value = it },
//            label = { Text("Phone Number") }
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//            value = passwordState.value,
//            onValueChange = { passwordState.value = it },
//            label = { Text("Password") },
//            visualTransformation = PasswordVisualTransformation()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//            value = confirmPasswordState.value,
//            onValueChange = { confirmPasswordState.value = it },
//            label = { Text("Confirm Password") },
//            visualTransformation = PasswordVisualTransformation()
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(
//            onClick = {
//            val phoneNumber = phoneNumberState.value
//            val password = passwordState.value
//            val confirmPassword = confirmPasswordState.value
//
//            if (password == confirmPassword) {
//            scope.launch {
//            val user = userRepository.getUserByPhoneNumber(phoneNumber)
//            if (user != null) {
//            showDialog.value = true
//            dialogMessage.value = "Confirm you want to change password for ${
//            (user.firstName+" "+user.lastNamme).uppercase(
//            Locale.ROOT
//            )
//            }"
//            } else {
//            showDialog.value = true
//            dialogMessage.value = "User not found"
//            }
//            }
//            } else {
//            showDialog.value = true
//            dialogMessage.value = "Password and Confirm Password do not match"
//            }
//            },
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//            ) {
//            Text("Change Password")
//            }
//
//            if (showDialog.value) {
//            AlertDialog(
//            onDismissRequest = { showDialog.value = false },
//            title = { Text("Alert") },
//            text = { Text(dialogMessage.value) },
//            confirmButton = {
//            Button(
//            onClick = {
//            scope.launch {
//            val phoneNumber = phoneNumberState.value
//            val password = passwordState.value
//
//            val result = userRepository.updateUserPassword(phoneNumber, password)
//            if (result) {
//            showDialog.value = false
//            dialogMessage.value = "Password updated successfully"
//            } else {
//            showDialog.value = false
//            dialogMessage.value = "Error updating password"
//            }
//            }
//            }
//            ) {
//            Text("Confirm")
//            }
//            },
//            dismissButton = {
//            Button(
//            onClick = { showDialog.value = false }
//            ) {
//            Text("Dismiss")
//            }
//            }
//            )
//            }
//            }
//            }
//
//
//
//class UserRepository() {
//    // ...
//    val db = FirebaseFirestore.getInstance()
//
//    private val usersCollection = db.collection("users")
//    suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
//        return suspendCoroutine { continuation ->
//                usersCollection
//                        .whereEqualTo("phoneNumber", phoneNumber)
//                        .get()
//                        .addOnSuccessListener { querySnapshot ->
//            if (querySnapshot.isEmpty) {
//                continuation.resume(null)
//            } else {
//                val user = querySnapshot.documents[0].toUser()
//                continuation.resume(user)
//            }
//        }
//                    .addOnFailureListener { exception ->
//                    continuation.resumeWithException(exception)
//            }
//        }
//    }
//
//    suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
//        var change_flag: Boolean = false
//        usersCollection
//                .whereEqualTo("phoneNumber", phoneNumber)
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//            for (document in querySnapshot.documents) {
//
//
//                document.reference.update("password", password)
//                        .addOnSuccessListener {
//                    // Update successful
//                    change_flag = true
//                }
//                            .addOnFailureListener { e ->
//                        // Handle any errors
//                        println("Error updating total welfare paid: $e")
//                }
//            }
//        }
//                .addOnFailureListener { e ->
//                // Handle any errors
//                println("Error getting documents: $e")
//        }
//        return change_flag
//    }
////            return suspendCoroutine { continuation ->
////                usersCollection
////                    .whereEqualTo("phoneNumber", phoneNumber)
////                    .get()
////                    .addOnSuccessListener { querySnapshot ->
////                        if (querySnapshot.isEmpty) {
////                            continuation.resume(false)
////                        } else {
////                            val userId = querySnapshot.documents[0].id
////                            val user = querySnapshot.documents[0].toObject(User::class.java)
////                            user?.password = password
////
////                            usersCollection
////                                .document(userId)
////                                .set(user!!)
////                                .addOnSuccessListener {
////                                    continuation.resume(true)
////                                }
////                                .addOnFailureListener { exception ->
////                                    continuation.resumeWithException(exception)
////                                }
////                        }
////                    }
////                    .addOnFailureListener { exception ->
////                        continuation.resumeWithException(exception)
////                    }
//    // }
//    //}to
//
//
//    fun DocumentSnapshot.toUser(): User {
//        val fname = getString("firstName") ?: ""
//        val sname = getString("lastName") ?:""
//
//
//
//
//
//        return User(fname,sname)
//    }
//
//    // ...
//}
//
//data class User(
//        val firstName: String,
//        val lastNamme:String,
//        )
//
