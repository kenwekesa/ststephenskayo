//package com.ack.ststephenskayo
//
//
//
//
//import android.app.Activity
//import android.content.Context
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.AlertDialog
//import androidx.compose.material.Button
//import androidx.compose.material.OutlinedTextField
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.google.firebase.firestore.DocumentReference
//import com.google.firebase.firestore.DocumentSnapshot
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FirebaseFirestoreException
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import java.util.Locale
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//import kotlin.coroutines.suspendCoroutine
//
//class PasswordManager: ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            PasswordManagerScreen(context = this@PasswordManager)
//        }
//    }
//}
//
//
//
//
//
//
//@Composable
//fun PasswordManagerScreen(context: Context) {
//    val phoneNumberState = remember { mutableStateOf("") }
//    val passwordState = remember { mutableStateOf("") }
//    val confirmPasswordState = remember { mutableStateOf("") }
//    val showDialog = remember { mutableStateOf(false) }
//    val dialogMessage = remember { mutableStateOf("") }
//    val showSuccessDialog = remember { mutableStateOf(false) }
//
//    val userRepository = UserRepository()
//
//    val scope = rememberCoroutineScope()
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .fillMaxHeight()
//    ) {
//        Column(
//            modifier = Modifier.align(Alignment.Center),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            OutlinedTextField(
//                value = phoneNumberState.value,
//                onValueChange = { phoneNumberState.value = it },
//                label = { Text("Phone Number") }
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // ... other UI elements ...
//
//            Button(
//                onClick = {
//                    val phoneNumber = phoneNumberState.value
//                    val password = passwordState.value
//                    val confirmPassword = confirmPasswordState.value
//
//                    scope.launch {
//                        val user = userRepository.getUserByPhoneNumber(phoneNumber)
//                        if (user != null) {
//                            showDialog.value = true
//                            dialogMessage.value =
//                                "Confirm you want to reset account for ${
//                                    (user.firstName + " " + user.lastNamme).uppercase(
//                                        Locale.ROOT
//                                    )
//                                }"
//                        } else {
//                            showNoUserMessage(context)
//                        }
//                    }
//                }
//            ) {
//                Text("Reset Account")
//            }
//
//            // ... other UI elements ...
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
//                                    val result = userRepository.updateUserPassword(
//                                        phoneNumber,
//                                        password
//                                    )
//                                    if (result) {
//                                        showresetSuccessMessage(context)
//                                    } else {
//                                        showresetFailureMessage(context)
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
//
//            if (showSuccessDialog.value) {
//                AlertDialog(
//                    onDismissRequest = { showSuccessDialog.value = false },
//                    title = { Text("Success") },
//                    text = { Text("Account successfully reset!") },
//                    confirmButton = {
//                        Button(
//                            onClick = {
//                                showSuccessDialog.value = false
//                                if (context is Activity) {
//                                    context.finish()
//                                }
//                                // Close the activity when the user clicks "OK"
//                                // You can replace this with the appropriate code to close the activity
//                            }
//                        ) {
//                            Text("OK")
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
//
//
//
//fun showresetSuccessMessage(context: Context) {
//    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
//        .setTitle("Reset Success")
//        .setMessage("The user account successfully reset. Login with phone number as your new password")
//        .setPositiveButton("OK") { _, _ ->
//            // Perform additional actions or close the dialog if needed
//
//            if (context is Activity) {
//                context.finish()
//            }
//        }
//        .create()
//
//    alertDialog.show()
//}
//
//
//
//
//private fun showNoUserMessage(context: Context) {
//    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
//        .setTitle("User Not Found")
//        .setMessage("No user found with the provided phone number.")
//        .setPositiveButton("OK") { _, _ ->
//            // Perform additional actions or close the dialog if needed
//        }
//        .create()
//
//    alertDialog.show()
//}
//
//
//
//private fun showresetFailureMessage(context: Context) {
//    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
//        .setTitle("Error reseting")
//        .setMessage("An error occured while resetting, try again later.")
//        .setPositiveButton("OK") { _, _ ->
//            // Perform additional actions or close the dialog if needed
//        }
//        .create()
//
//    alertDialog.show()
//}
//
//
//
//class UserRepository() {
//    // ...
//    val db = FirebaseFirestore.getInstance()
//
//    private val usersCollection = db.collection("users")
//        suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
//            return suspendCoroutine { continuation ->
//                usersCollection
//                    .whereEqualTo("phoneNumber", phoneNumber)
//                    .get()
//                    .addOnSuccessListener { querySnapshot ->
//                        if (querySnapshot.isEmpty) {
//                            continuation.resume(null)
//                        } else {
//                            val user = querySnapshot.documents[0].toUser()
//                            continuation.resume(user)
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        continuation.resumeWithException(exception)
//                    }
//            }
//        }
//
////        suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
////            var change_flag: Boolean = false
////
////            val updates = hashMapOf<String, Any>(
////                "password" to phoneNumber,
////                "user_activated" to false
////            )
////            usersCollection
////                .whereEqualTo("phoneNumber", phoneNumber)
////                .get()
////                .addOnSuccessListener { querySnapshot ->
////                    for (document in querySnapshot.documents) {
////                        document.reference.update(updates)
////                            .addOnSuccessListener {
////                                // Update successful
////                                change_flag = true
////                            }
////                            .addOnFailureListener { e ->
////                                // Handle any errors
////                                println("Error resetting account $e")
////                            }
////                    }
////                }
////                .addOnFailureListener { e ->
////                    // Handle any errors
////                    println("Error getting documents: $e")
////                }
////          return change_flag
////        }
//
//    fun Any?.toAny(): Any? {
//        return when (this) {
//            is String,
//            is Number,
//            is Boolean -> this
//            is Map<*, *> -> this.mapValues { it.value.toAny() }
//            is List<*> -> this.map { it.toAny() }
//            is DocumentReference -> this
//            is FieldValue -> this
//            else -> null
//        }
//    }
//    suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
//        try {
//            val updates = hashMapOf(
//                "password" to phoneNumber.toAny(),
//                "user_activated" to false.toAny()
//            )
//
//            val querySnapshot = usersCollection
//                .whereEqualTo("phoneNumber", phoneNumber)
//                .get()
//                .await() // Use await() to suspend and wait for the result
//
//            for (document in querySnapshot.documents) {
//                document.reference.update(updates).await() // Suspend and wait for the update to complete
//            }
//
//            return true // If the loop completes without any exceptions, the updates were successful
//        } catch (e: FirebaseFirestoreException) {
//            // Handle any errors
//            println("Error updating user password: $e")
//            return false
//        }
//    }
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
//    val firstName: String,
//    val lastNamme:String,
//
//
//
//)
//

package com.ack.ststephenskayo

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

//class PasswordManager : ComponentActivity() {
//
//    private lateinit var sharedPrefs: SharedPreferences
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//
//        val phoneNumber = sharedPrefs.getString("phoneNumber", null) //
//        val memberNumber = sharedPrefs.getString("memberNumber", null) //
//        val firstname = sharedPrefs.getString("firstname", null)
//        val lastname = sharedPrefs.getString("lastname", null)
//        val usertype = sharedPrefs.getString("usertype", null)
//        setContent {
//            PasswordManagerScreen(context = this@PasswordManager, usertype.toString())
//        }
//    }
//}
//
//@Composable
//fun PasswordManagerScreen(context: Context, usertype:String) {
//    val phoneNumberState = remember { mutableStateOf("") }
//    val passwordState = remember { mutableStateOf("") }
//    val confirmPasswordState = remember { mutableStateOf("") }
//    val showDialog = remember { mutableStateOf(false) }
//    val dialogMessage = remember { mutableStateOf("") }
//    val showSuccessDialog = remember { mutableStateOf(false) }
//    val showRoleDropdown = remember { mutableStateOf(false) }
//    val selectedRole = remember { mutableStateOf("") }
//    val currentUserRole = remember { mutableStateOf("super_admin") } // This should be fetched from your auth system
//
//    val userRepository = UserRepository()
//    val scope = rememberCoroutineScope()
//
//    val roles = listOf("admin", "super_admin", "twenty_admin", "welfare_admin", "secretary_admin")
//
//
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .fillMaxHeight()
//    ) {
//        Column(
//            modifier = Modifier.align(Alignment.Center),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            OutlinedTextField(
//                value = phoneNumberState.value,
//                onValueChange = { phoneNumberState.value = it },
//                label = { Text("Phone Number") }
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//
//
//
//            Button(
//                onClick = {
//                    val phoneNumber = phoneNumberState.value
//                    val password = passwordState.value
//                    val confirmPassword = confirmPasswordState.value
//
//                    scope.launch {
//                        val user = userRepository.getUserByPhoneNumber(phoneNumber)
//                        if (user != null) {
//                            showDialog.value = true
//                            dialogMessage.value =
//                                "Confirm you want to reset account for ${
//                                    (user.firstName + " " + user.lastNamme).uppercase(
//                                        Locale.ROOT
//                                    )
//                                }"
//                        } else {
//                            showNoUserMessage(context)
//                        }
//                    }
//                }
//            ) {
//                Text("Reset Account")
//            }
//
//            if (usertype == "super_admin") {
//                Button(
//                    onClick = { showRoleDropdown.value = !showRoleDropdown.value },
//                    modifier = Modifier.padding(top = 16.dp)
//                ) {
//                    Text("Change Role")
//                }
//
//                if (showRoleDropdown.value) {
//                    DropdownMenu(
//                        expanded = showRoleDropdown.value,
//                        onDismissRequest = { showRoleDropdown.value = false }
//                    ) {
//                        roles.forEach { role ->
//                            DropdownMenuItem(onClick = {
//                                selectedRole.value = role
//                                showRoleDropdown.value = false
//                                scope.launch {
//                                    val phoneNumber = phoneNumberState.value
//                                    val result = userRepository.updateUserRole(phoneNumber, role)
//                                    if (result) {
//                                        showSuccessDialog.value = true
//                                    } else {
//                                        showresetFailureMessage(context)
//                                    }
//                                }
//                            }) {
//                                Text(role.replace("_", " ").capitalize(Locale.ROOT))
//                            }
//                        }
//                    }
//                }
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
//                                    val result = userRepository.updateUserPassword(
//                                        phoneNumber,
//                                        password
//                                    )
//                                    if (result) {
//                                        showresetSuccessMessage(context)
//                                    } else {
//                                        showresetFailureMessage(context)
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
//
//            if (showSuccessDialog.value) {
//                AlertDialog(
//                    onDismissRequest = { showSuccessDialog.value = false },
//                    title = { Text("Success") },
//                    text = { Text("Action successfully completed!") },
//                    confirmButton = {
//                        Button(
//                            onClick = {
//                                showSuccessDialog.value = false
//                                if (context is Activity) {
//                                    context.finish()
//                                }
//                            }
//                        ) {
//                            Text("OK")
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
//
//fun showresetSuccessMessage(context: Context) {
//    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
//        .setTitle("Reset Success")
//        .setMessage("The user account was successfully reset. Login with phone number as your new password")
//        .setPositiveButton("OK") { _, _ ->
//            if (context is Activity) {
//                context.finish()
//            }
//        }
//        .create()
//
//    alertDialog.show()
//}
//
//private fun showNoUserMessage(context: Context) {
//    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
//        .setTitle("User Not Found")
//        .setMessage("No user found with the provided phone number.")
//        .setPositiveButton("OK") { _, _ -> }
//        .create()
//
//    alertDialog.show()
//}
//
//private fun showresetFailureMessage(context: Context) {
//    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
//        .setTitle("Error Resetting")
//        .setMessage("An error occurred while resetting, try again later.")
//        .setPositiveButton("OK") { _, _ -> }
//        .create()
//
//    alertDialog.show()
//}
//
//class UserRepository {
//    private val db = FirebaseFirestore.getInstance()
//    private val usersCollection = db.collection("users")
//
//    suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
//        return try {
//            val querySnapshot = usersCollection
//                .whereEqualTo("phoneNumber", phoneNumber)
//                .get()
//                .await()
//            if (querySnapshot.isEmpty) {
//                null
//            } else {
//                querySnapshot.documents[0].toUser()
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
//        return try {
//            val updates = hashMapOf(
//                "password" to phoneNumber,
//                "user_activated" to false
//            )
//            val querySnapshot = usersCollection
//                .whereEqualTo("phoneNumber", phoneNumber)
//                .get()
//                .await()
//            for (document in querySnapshot.documents) {
//                document.reference.update(updates as Map<String, Any>).await()
//            }
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    suspend fun updateUserRole(phoneNumber: String, role: String): Boolean {
//        return try {
//            val updates = hashMapOf(
//                "user_role" to role
//            )
//            val querySnapshot = usersCollection
//                .whereEqualTo("phoneNumber", phoneNumber)
//                .get()
//                .await()
//            for (document in querySnapshot.documents) {
//                document.reference.update(updates as Map<String, Any>).await()
//            }
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    private fun DocumentSnapshot.toUser(): User {
//        val firstName = getString("firstName") ?: ""
//        val lastName = getString("lastName") ?: ""
//        return User(firstName, lastName)
//    }
//}
//
//data class User(
//    val firstName: String,
//    val lastNamme: String
//)


import com.google.firebase.auth.FirebaseAuth


class PasswordManager : ComponentActivity() {
    private lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val phoneNumber = sharedPrefs.getString("phoneNumber", null) //
        val memberNumber = sharedPrefs.getString("memberNumber", null) //
        val firstname = sharedPrefs.getString("firstname", null)
        val lastname = sharedPrefs.getString("lastname", null)
        val usertype = sharedPrefs.getString("usertype", null)

        setContent {
            PasswordManagerScreen(context = this@PasswordManager, usertype.toString())
        }
    }
}

@Composable
fun PasswordManagerScreen(context: Context, usertype:String) {
    val phoneNumberState = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val showSuccessDialog = remember { mutableStateOf(false) }
    val showRoleDropdown = remember { mutableStateOf(false) }
    val selectedRole = remember { mutableStateOf("") }
    val showConfirmRoleChangeDialog = remember { mutableStateOf(false) }
    val currentUserRole = remember { mutableStateOf("super_admin") } // This should be fetched from your auth system
    val currentUserPhoneNumber = remember { mutableStateOf("") } // This should be fetched from your auth system

    val userRepository = UserRepository()
    val scope = rememberCoroutineScope()

    val roles = listOf("admin", "super_admin", "twenty_admin", "welfare_admin", "secretary_admin")

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

            Button(
                onClick = {
                    val phoneNumber = phoneNumberState.value

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

            if (usertype == "super_admin") {
                Button(
                    onClick = {
                        showRoleDropdown.value = !showRoleDropdown.value
                        val phoneNumber = phoneNumberState.value

                        scope.launch {
                            val user = userRepository.getUserByPhoneNumber(phoneNumber)
                            if (user != null) {
                                showDialog.value = true
                                dialogMessage.value =
                                    "Confirm you want change user role of  ${
                                        (user.firstName + " " + user.lastNamme).uppercase(
                                            Locale.ROOT
                                        )
                                    }"
                            } else {
                                showNoUserMessage(context)
                            }
                        }

                              },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Change Role")
                }

                if (showRoleDropdown.value) {
                    DropdownMenu(
                        expanded = showRoleDropdown.value,
                        onDismissRequest = { showRoleDropdown.value = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(onClick = {
                                selectedRole.value = role
                                showRoleDropdown.value = false
                                showConfirmRoleChangeDialog.value = true
                            }) {
                                Text(role.replace("_", " ").capitalize(Locale.ROOT))
                            }
                        }
                    }
                }
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

                                    val result = userRepository.updateUserPassword(
                                        phoneNumber,
                                        phoneNumber // Use phone number as the new password
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

            if (showConfirmRoleChangeDialog.value) {
                val phoneNumber = phoneNumberState.value



                AlertDialog(
                    onDismissRequest = { showConfirmRoleChangeDialog.value = false },
                    title = { Text("Confirm Role Change") },
                    text = { Text(dialogMessage.value+" to ${selectedRole.value.replace("_", " ").capitalize(Locale.ROOT)}?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    val phoneNumber = phoneNumberState.value
                                    val result = userRepository.updateUserRole(phoneNumber, selectedRole.value)
                                    if (result) {
                                        if (currentUserPhoneNumber.value == phoneNumber && currentUserRole.value == "super_admin" && selectedRole.value != "super_admin") {
                                            FirebaseAuth.getInstance().signOut()
                                            (context as? Activity)?.finish()
                                        } else {
                                            showSuccessDialog.value = true
                                        }
                                    } else {
                                        showresetFailureMessage(context)
                                    }
                                    showConfirmRoleChangeDialog.value = false
                                }
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showConfirmRoleChangeDialog.value = false }
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
                    text = { Text("Action successfully completed!") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog.value = false
                                if (context is Activity) {
                                    context.finish()
                                }
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
        .setMessage("The user account was successfully reset. Login with phone number as your new password")
        .setPositiveButton("OK") { _, _ ->
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
        .setPositiveButton("OK") { _, _ -> }
        .create()

    alertDialog.show()
}

private fun showresetFailureMessage(context: Context) {
    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(context)
        .setTitle("Error Resetting")
        .setMessage("An error occurred while resetting, try again later.")
        .setPositiveButton("OK") { _, _ -> }
        .create()

    alertDialog.show()
}

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun getUserByPhoneNumber(phoneNumber: String): User? {
        return try {
            val querySnapshot = usersCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .await()
            if (querySnapshot.isEmpty) {
                null
            } else {
                querySnapshot.documents[0].toUser()
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserPassword(phoneNumber: String, password: String): Boolean {
        return try {
            val updates = hashMapOf(
                "password" to phoneNumber,
                "user_activated" to false
            )
            val querySnapshot = usersCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .await()
            for (document in querySnapshot.documents) {
                document.reference.update(updates as Map<String, Any>).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserRole(phoneNumber: String, role: String): Boolean {
        return try {
            val updates = hashMapOf(
                "usertype" to role
            )
            val querySnapshot = usersCollection
                .whereEqualTo("phoneNumber", phoneNumber)
                .get()
                .await()
            for (document in querySnapshot.documents) {
                document.reference.update(updates as Map<String, Any>).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun DocumentSnapshot.toUser(): User {
        val firstName = getString("firstName") ?: ""
        val lastName = getString("lastName") ?: ""
        return User(firstName, lastName)
    }
}

data class User(
    val firstName: String,
    val lastNamme: String
)
