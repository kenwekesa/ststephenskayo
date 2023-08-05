package com.ack.ststephenskayo

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateUser : ComponentActivity() {
    private val updateUserViewModel: UpdateUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UpdateUserView(updateUserViewModel)
            }
        }
    }
}

class UpdateUserViewModel : ViewModel() {
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val fellowship = mutableStateOf("")

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

    // Function to fetch existing user details based on phoneNumber
    fun fetchExistingUserDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            val userSnapshot = fetchUser(phoneNumber.value)
            if (userSnapshot != null && userSnapshot.exists()) {
                val user = userSnapshot.data
                firstName.value = user?.get("firstName") as? String ?: ""
                lastName.value = user?.get("lastName") as? String ?: ""
                fellowship.value = user?.get("fellowship") as? String ?: ""
                middleName.value = user?.get("middleName") as? String ?: ""
                birthDate.value = user?.get("birthDate") as? String ?: ""
                birthMonth.value = user?.get("birthMonth") as? String ?: ""
                // Update other fields as needed
            } else {
                // User not found or error occurred while fetching data
                // You can show an error message or handle this case accordingly
            }
        }
    }

    fun performUpdate() {
        // Check if the phone number is valid
        if (!isPhoneNumberValid(phoneNumber.value)) {
            submissionStatus.value = SubmissionStatus.ERROR
            submissionMessage.value = "Invalid phone number"
            return
        }

        // Fetch the user document from Firestore based on the phoneNumber
        viewModelScope.launch {
            val userExists = fetchUser(phoneNumber.value)
            if (userExists != null) {
                if (userExists.exists()) {
                    try {
                        val db = FirebaseFirestore.getInstance()
                        val user = hashMapOf(
                            // Update user properties...
                            "firstName" to firstName.value,
                            "lastName" to lastName.value,
                            "fellowship" to fellowship.value,
                            "middleName" to middleName.value,
                            "birthDate" to birthDate.value,
                            "birthMonth" to birthMonth.value
                            // Add other fields as needed
                        )

                        db.collection("users")
                            .document((firstName.value + "_" + middleName.value + "_" + lastName.value).replace("__", "_"))
                            .update(user as Map<String, Any>)
                            .addOnSuccessListener {
                                // Update successful
                                submissionStatus.value = SubmissionStatus.SUCCESS
                                submissionMessage.value = "User details updated successfully!"
                            }
                            .addOnFailureListener { e ->
                                // Update failed
                                submissionStatus.value = SubmissionStatus.ERROR
                                submissionMessage.value = "Error occurred during update."
                            }
                    } catch (e: Exception) {
                        // Error handling and logging
                        submissionStatus.value = SubmissionStatus.ERROR
                        submissionMessage.value = "Error occurred during update."
                        e.printStackTrace()
                    }
                } else {
                    submissionStatus.value = SubmissionStatus.ERROR
                    submissionMessage.value = "User with this phone number does not exist"
                }
            }
        }
    }
    // ... (performUpdate() function and other existing functions) ...

    private suspend fun fetchUser(phoneNumber: String): DocumentSnapshot? {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        return try {
            val querySnapshot = usersCollection.whereEqualTo("phoneNumber", phoneNumber).get().await()
            if (!querySnapshot.isEmpty) {
                querySnapshot.documents.first()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("\\d{10}"))
    }
}

@Composable
fun UpdateUserView(viewModel: UpdateUserViewModel = viewModel()) {


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

    // Variables for dropdown visibility
    // Variables for dropdown visibility
    var dayMenuVisible by remember {mutableStateOf(false)}
    var monthMenuVisible by remember {mutableStateOf(false)}


    var buttonClicked by remember { mutableStateOf(false) }
    var datePickerClicked by remember { mutableStateOf(false)}
    var dayPickerClicked by remember { mutableStateOf(false) }
    var monthPickerClicked by remember { mutableStateOf(false) }

    val firstNameError = remember { mutableStateOf(false) }
    val lastNameError = remember { mutableStateOf(false) }
    val fellowshipError = remember { mutableStateOf(false) }
    val phoneNumberError = remember { mutableStateOf(false) }
    val datePickerError = remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = viewModel.firstName.value,
            onValueChange = { viewModel.firstName.value = it },
            label = { Text("First Name") },
            isError = firstNameError.value, // Check if it's empty
            singleLine = true, // Set singleLine to improve UI for mandatory fields
            // Add styling to indicate error in red
            textStyle = if (firstNameError.value) TextStyle(color = Color.Red) else LocalTextStyle.current
        )
        OutlinedTextField(
            value = viewModel.middleName.value,
            onValueChange = { viewModel.middleName.value = it },
            label = { Text("Middle Name") },
            //isError = viewModel.firstName.value.isBlank(),
            singleLine = true // Set singleLine to improv
        )
        OutlinedTextField(
            value = viewModel.lastName.value,
            onValueChange = { viewModel.lastName.value = it },
            label = { Text("Last Name") },
            isError = lastNameError.value,//buttonClicked && viewModel.lastName.value.isBlank(),
            singleLine = true,
            textStyle = if (lastNameError.value) TextStyle(color = Color.Red) else LocalTextStyle.current

        )

        OutlinedTextField(
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.phoneNumber.value = it },
            label = { Text("Phone Number") },
            isError = phoneNumberError.value || !viewModel.isPhoneNumberValid(viewModel.phoneNumber.value),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = if (phoneNumberError.value || !viewModel.isPhoneNumberValid(viewModel.phoneNumber.value)) TextStyle(color = Color.Red) else LocalTextStyle.current

            //visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = viewModel.fellowship.value,
            onValueChange = { viewModel.fellowship.value = it },
            label = { Text("Fellowship") },
            isError = fellowshipError.value,//buttonClicked && viewModel.fellowship.value.isBlank(),
            singleLine = true,
            textStyle = if (fellowshipError.value) TextStyle(color = Color.Red) else LocalTextStyle.current

        )

        // Date Joined Button and Text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Button(
                onClick = { mDatePickerDialog.show()
                    datePickerClicked = true
                },
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

        // Dropdown Menus
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                fontSize = 11.sp,
                text ="Birth date and month: "
            )
            Box {
                OutlinedButton(
                    onClick = {
                        dayPickerClicked = true
                        dayMenuVisible = true },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(selectedDayOfBirth.value ?: "Day")
                }
                DropdownMenu(
                    expanded = dayMenuVisible,
                    onDismissRequest = { dayMenuVisible = false },
                    modifier = Modifier
                        .width(100.dp) // Adjust the width here
                        .height(200.dp) // Adjust the height here
                ) {
                    dayOfBirthOptions.forEach { day ->
                        DropdownMenuItem(
                            onClick = {
                                selectedDayOfBirth.value = day
                                dayMenuVisible = false
                            }
                        ) {
                            Text(text = day)
                        }
                    }
                }
            }
            Box {
                OutlinedButton(
                    onClick = {

                        monthPickerClicked = true
                        monthMenuVisible = true
                    }
                ) {
                    Text(selectedMonthOfBirth.value ?: "Month")
                }
                DropdownMenu(
                    expanded = monthMenuVisible,
                    onDismissRequest = { monthMenuVisible = false },
                    modifier = Modifier
                        .width(100.dp) // Adjust the width here
                        .height(200.dp) // Adjust the height here
                ) {
                    monthOfBirthOptions.forEach { month ->
                        DropdownMenuItem(
                            onClick = {
                                selectedMonthOfBirth.value = month
                                monthMenuVisible = false
                            }
                        ) {
                            Text(text = month)
                        }
                    }
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }


        Button(
            onClick = {
                // Check if all mandatory fields are filled
                if (viewModel.firstName.value.isBlank()) {
                    firstNameError.value = true
                } else {
                    firstNameError.value = false
                }

                if (viewModel.lastName.value.isBlank()) {
                    lastNameError.value = true
                } else {
                    lastNameError.value = false
                }

                if (viewModel.fellowship.value.isBlank()) {
                    fellowshipError.value = true
                } else {
                    fellowshipError.value = false
                }

                if (viewModel.phoneNumber.value.isBlank()) {
                    phoneNumberError.value = true
                } else {
                    phoneNumberError.value = false
                }
                // Similarly, check and set error states for other fields



                if (viewModel.firstName.value.isBlank() ||
                    viewModel.lastName.value.isBlank() ||
                    viewModel.phoneNumber.value.isBlank()||
                    !datePickerClicked ||
                    !dayPickerClicked ||
                    !monthPickerClicked
                ) {

                    // Update the errorMessage with the error message
                    errorMessage = "Please fill all mandatory fields."

                } else {
                    // Perform sign-in logic here
                    viewModel.performUpdate()
                }
            },
            modifier = Modifier.fillMaxWidth().
            padding(top = 16.dp).
            padding(horizontal = 40.dp)
        ) {
            Text("Add Member")
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
        } else if (submissionStatus == UpdateUserViewModel.SubmissionStatus.ERROR) {
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
    }
}
