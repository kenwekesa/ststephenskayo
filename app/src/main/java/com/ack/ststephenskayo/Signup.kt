package com.ack.ststephenskayo
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import ch.benlu.composeform.FieldState
//import ch.benlu.composeform.Form
//import ch.benlu.composeform.FormField
//
//import ch.benlu.composeform.fields.*;
//import ch.benlu.composeform.validators.MinLengthValidator
//
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//
//
//
//
//class Signup : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val viewModel = MainViewModel() // Create an instance of the MainViewModel
//
//        setContent {
//            SingleTextField(viewModel = viewModel) // Pass the view model to the composable function
//        }
//
//    }
//
//
//}
//
//class MainForm : Form() {
//    override fun self(): Form {
//        return this
//    }
//
//    @FormField
//    val name = FieldState(
//        state = mutableStateOf<String?>(null),
//        validators = mutableListOf(
//            MinLengthValidator(4, "The name must be at least 4 characters")
//        )
//    )
//}
//
//class MainViewModel : ViewModel() {
//    var form = MainForm()
//}
//
//@Composable
//fun SingleTextField(viewModel: MainViewModel = viewModel()) {
//    Column {
//        TextField(
//            label = "Name",
//            form = viewModel.form,
//            fieldState = viewModel.form.name,
//        ).Field()
//    }
//}
//

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.setContent
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.firestore.FirebaseFirestore
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignInView()
        }
    }
}


class SignInViewModel : ViewModel() {
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val middleName = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val dateJoined = mutableStateOf("")

    val birthDate = mutableStateOf("")
    val birthMonth = mutableStateOf("")


    val submissionStatus = mutableStateOf<SubmissionStatus?>(null)
    val submissionMessage = mutableStateOf<String?>(null)

    enum class SubmissionStatus {
        SUCCESS,
        ERROR
    }

    fun performSignIn() {
        generateUniqueNumber { uniqueNumber ->
            // Use the uniqueNumber here
            // Rest of the function...



        try {
            val db = FirebaseFirestore.getInstance()
            val user = hashMapOf(
                "firstName" to firstName.value,
                "lastName" to lastName.value,
                "password" to phoneNumber.value,
                "dateJoined" to dateJoined.value,
                "phoneNumber" to phoneNumber.value,
                "middleName" to middleName.value,
                "birthDate" to birthDate.value,
                "birthMonth" to birthMonth.value,
                "total_welfare_paid" to 0,
                "total_twenty_paid" to 0,
                "usertype" to "member",
                "memberNumber" to   uniqueNumber // Add unique number field
                // Add other fields as needed
            )

            db.collection("users")
                .document(firstName.value)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    // Sign-in and data submission successful
                    // Handle any necessary actions or navigate to the next screen
                    updateLastUniqueNumber(uniqueNumber)
                    submissionStatus.value = SubmissionStatus.SUCCESS
                    submissionMessage.value = "Member added successfully!"
                }
                .addOnFailureListener { e ->
                    // Sign-in and data submission failed
                    // Handle error case and display appropriate message

                    submissionStatus.value = SubmissionStatus.ERROR
                    submissionMessage.value = "Error occurred during submission."
                }
        }
        catch (e: Exception) {
            // Error handling and logging
            Log.e("Signup", "Error submitting data: ${e.message}", e)
        }
    }}

    private fun updateLastUniqueNumber(newUniqueNumber: String) {
        try {
            val db = FirebaseFirestore.getInstance()
            val configDocRef = db.collection("config").document("configDocument")

            val configCollectionSnapshot = db.collection("config").get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        db.collection("config").document("configDocument")
                            .set(mapOf("lastUniqueNumber" to newUniqueNumber))
                            .addOnSuccessListener {
                                // Collection and document created successfully
                            }
                            .addOnFailureListener { e ->
                                // Error handling for document creation
                                Log.e("Signup", "Error creating config document: ${e.message}", e)
                            }
                    } else {
                        db.runTransaction { transaction ->
                            val docSnapshot = transaction.get(configDocRef)
                            transaction.update(configDocRef, "lastUniqueNumber", newUniqueNumber)
                        }
                            .addOnSuccessListener {
                                // Update successful
                            }
                            .addOnFailureListener { e ->
                                // Error handling for transaction
                                Log.e("Signup", "Error updating last unique number: ${e.message}", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Error handling for collection retrieval
                    Log.e("Signup", "Error retrieving config collection: ${e.message}", e)
                }

        } catch (e: Exception) {
            // Error handling and logging
            Log.e("Signup", "Error updating last unique number: ${e.message}", e)
        }
    }


    private fun generateUniqueNumber(callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val configDocRef = db.collection("config").document("configDocument")

        configDocRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    val uniqueNumber = document.getString("lastUniqueNumber") ?: "KYO000"
                    val number = uniqueNumber.substring(3).toInt() + 1
                    val incrementedNumber = number.toString().padStart(3, '0')
                    val newUniqueNumber = "KYO$incrementedNumber"
                    callback(newUniqueNumber) // Invoke the callback with the new unique number
                } else {
                    Log.e("Signup", "Config document not found.")
                    callback("KYO000") // Invoke the callback with a default unique number when the document is not found
                }
            } else {
                Log.e("Signup", "Error getting config document: ${task.exception?.message}", task.exception)
                callback("KYO000") // Invoke the callback with a default unique number in case of error
            }
        }
    }







    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("\\d{10}"))
    }
}



@Composable
fun SignInView(viewModel: SignInViewModel = viewModel()) {


   // val firstName = viewModel.firstName.value
   // val lastName = viewModel.lastName.value
   // val phoneNumber = viewModel.phoneNumber.value
    val submissionStatus = viewModel.submissionStatus.value
    val submissionMessage = viewModel.submissionMessage.value

   // val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
    val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
    val formattedDate = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }
    //val selectedDate = remember { Calendar.getInstance() }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
   // val formattedDate = remember { mutableStateOf(dateFormatter.format(selectedDate.value.time)) }

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



    val dayOfBirthOptions = (1..31).map { it.toString() }
    val monthOfBirthOptions = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    val selectedDayOfBirth = remember { mutableStateOf(dayOfBirthOptions.first()) }
    val selectedMonthOfBirth = remember { mutableStateOf(monthOfBirthOptions.first()) }

    Column(Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        OutlinedTextField(
            value = viewModel.firstName.value,
            onValueChange = { viewModel.firstName.value = it },
            label = { Text("First Name") }
        )
        OutlinedTextField(
            value = viewModel.middleName.value,
            onValueChange = { viewModel.middleName.value = it },
            label = { Text("Middle Name") }
        )
        OutlinedTextField(
            value = viewModel.lastName.value,
            onValueChange = { viewModel.lastName.value = it },
            label = { Text("Last Name") }
        )

        OutlinedTextField(
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.phoneNumber.value = it },
            label = { Text("Phone Number") },
            isError = !viewModel.isPhoneNumberValid(viewModel.phoneNumber.value),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            //visualTransformation = PasswordVisualTransformation()
        )



        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Button(
                onClick = { mDatePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
            ) {
                Text(text = buttonText, color = Color.White)
            }
            Text(
                text = formattedDate.value,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 8.dp)
            )
        }



        Row(verticalAlignment = Alignment.CenterVertically) {
            DropdownMenu(
                expanded = false, // Change to true to initially show the dropdown
                onDismissRequest = { /* Dismiss the dropdown */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                dayOfBirthOptions.forEach { day ->
                    DropdownMenuItem(onClick = { selectedDayOfBirth.value = day }) {
                        Text(text = day)
                    }
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            DropdownMenu(
                expanded = false, // Change to true to initially show the dropdown
                onDismissRequest = { /* Dismiss the dropdown */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                monthOfBirthOptions.forEach { month ->
                    DropdownMenuItem(onClick = { selectedMonthOfBirth.value = month }) {
                        Text(text = month)
                    }
                }
            }
        }




        viewModel.dateJoined.value = formattedDate.value;
        viewModel.birthMonth.value = selectedMonthOfBirth.value
        viewModel.birthDate.value = selectedDayOfBirth.value




        if (submissionStatus == SignInViewModel.SubmissionStatus.SUCCESS) {
            AlertDialog(
                onDismissRequest = { viewModel.submissionStatus.value = null },
                title = { Text("Success") },
                text = { Text(submissionMessage ?: "") },
                confirmButton = {
                    Button(onClick = { viewModel.submissionStatus.value = null }) {
                        Text("OK")
                    }
                }
            )
        } else if (submissionStatus == SignInViewModel.SubmissionStatus.ERROR) {
            AlertDialog(
                onDismissRequest = { viewModel.submissionStatus.value = null },
                title = { Text("Error") },
                text = { Text(submissionMessage ?: "") },
                confirmButton = {
                    Button(onClick = { viewModel.submissionStatus.value = null }) {
                        Text("OK")
                    }
                }
            )
        }
        Button(
            onClick = {
                // Perform sign-in logic here
                viewModel.performSignIn()
            },
            modifier = Modifier.fillMaxWidth().
            padding(top = 16.dp).
            padding(horizontal = 40.dp)
        ) {
            Text("Add Member")
        }
    }
}


