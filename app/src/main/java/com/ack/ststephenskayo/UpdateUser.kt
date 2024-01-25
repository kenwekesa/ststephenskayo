package com.ack.ststephenskayo

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
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
        // Retrieve the data sent from the previous activity
        val phoneNumber = intent.getStringExtra("phoneNumber")

        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                UpdateUserView(updateUserViewModel, phoneNumber.toString())
            }
        }

        // Fetch the existing user details
        updateUserViewModel.fetchExistingUserDetails(phoneNumber)


    }
}

class UpdateUserViewModel : ViewModel() {
    val firstName = mutableStateOf("")
    val lastName = mutableStateOf("")
    val fellowship = mutableStateOf("")

    val middleName = mutableStateOf("")
    val phoneNumber = mutableStateOf("")
    val dateJoined = mutableStateOf("")
    val fieldOfStudy = mutableStateOf("")
    val occupation = mutableStateOf("")
    val openingTwentyBal = mutableStateOf("")
    val openingWelBal = mutableStateOf("")

    val birthDate = mutableStateOf("")
    val birthMonth = mutableStateOf("")

    val submissionStatus = mutableStateOf<SubmissionStatus?>(null)
    val submissionMessage = mutableStateOf<String?>(null)

    enum class SubmissionStatus {
        SUCCESS,
        ERROR
    }



    // Fetch existing user details based on phoneNumber
    fun fetchExistingUserDetails(phoneNo: String?) {
        phoneNo?.let {
            viewModelScope.launch(Dispatchers.IO) {
                // Fetch user details based on the phone number
                val userSnapshot = fetchUser(phoneNo)
                if (userSnapshot != null && userSnapshot.exists()) {
                    val user = userSnapshot.data
                    // Update the mutable state variables with fetched data
                    firstName.value = user?.get("firstName") as? String ?: ""
                    lastName.value = user?.get("lastName") as? String ?: ""
                    fellowship.value = user?.get("fellowship") as? String ?: ""
                    middleName.value = user?.get("middleName") as? String ?: ""
                    dateJoined.value = user?.get("dateJoined") as? String ?:""
                    birthDate.value = user?.get("birthDate") as? String ?:""
                    birthMonth.value = user?.get("birthMonth") as? String ?:""
                    occupation.value = user?.get("occupation") as? String ?:""
                    fieldOfStudy.value = user?.get("fieldOfStudy") as? String ?:""
                    openingTwentyBal.value = user?.get("openingTwentyBal") as? String ?:""
                    openingWelBal.value = user?.get("openingWelfareBal") as? String ?:""

                    phoneNumber.value = phoneNo
                    // Update other fields as needed
                }
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

                    val usrr = userExists.data
                    val existingFirstName = usrr?.get("firstName") as? String ?: ""
                    val existingMiddleName = usrr?.get("middleName") as? String ?: ""
                    val existingLastName = usrr?.get("lastName") as? String ?: ""

                    val documentPath = userExists.id

                    try {
                        val db = FirebaseFirestore.getInstance()
                        val user = hashMapOf(
                            // Update user properties...
                            "firstName" to firstName.value,
                            "lastName" to lastName.value,
                            "fellowship" to fellowship.value,
                            "middleName" to middleName.value,
                            "birthDate" to birthDate.value,
                            "birthMonth" to birthMonth.value,
                            "phoneNumber" to phoneNumber.value,
                            "dateJoined" to dateJoined.value,
                            "occupation" to occupation.value,
                            "openingTwentyBal" to openingTwentyBal.value,
                            "openingWelfareBal" to openingWelBal.value,
                            "fieldOfStudy" to fieldOfStudy.value,

                            // Add other fields as needed
                        )

                        db.collection("users")
                            .document(documentPath)
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
fun UpdateUserView(viewModel: UpdateUserViewModel = viewModel(), phoneNumber: String) {

//
//    // val firstName = viewModel.firstName.value
//    // val lastName = viewModel.lastName.value
//    // val phoneNumber = viewModel.phoneNumber.value
//    val submissionStatus = viewModel.submissionStatus.value
//    val submissionMessage = viewModel.submissionMessage.value
//
//    // val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
//    val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
//    val formattedDate = remember { mutableStateOf("") }
//    val showDialog = remember { mutableStateOf(false) }
//    //val selectedDate = remember { Calendar.getInstance() }
//    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
//    // val formattedDate = remember { mutableStateOf(dateFormatter.format(selectedDate.value.time)) }
//
//    val mYear: Int
//    val mMonth: Int
//    val mDay: Int
//    val context = LocalContext.current
//
//    // Initializing a Calendar
//    val mCalendar = Calendar.getInstance()
//
//    // Fetching current year, month and day
//    mYear = mCalendar.get(Calendar.YEAR)
//    mMonth = mCalendar.get(Calendar.MONTH)
//    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
//
//    formattedDate.value = viewModel.dateJoined.value;
//
//    // Set initial value of formattedDate
//    //formattedDate.value = viewModel.initialFormattedDate.value
//
//    var buttonText = "Date Joined"
//
//    val mDatePickerDialog = DatePickerDialog(
//        context,
//        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
//           // formattedDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
//            val newFormattedDate = "$mDayOfMonth/${mMonth + 1}/$mYear"
//            formattedDate.value = newFormattedDate
//            viewModel.dateJoined.value = newFormattedDate
//        }, mYear, mMonth, mDay
//    )
//
//
//
//    val dayOfBirthOptions = (1..31).map { it.toString() }
//    val monthOfBirthOptions = listOf(
//        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
//        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
//    )
//    val selectedDayOfBirth = remember { mutableStateOf(dayOfBirthOptions.first()) }
//    val selectedMonthOfBirth = remember { mutableStateOf(monthOfBirthOptions.first()) }
//
//    // Variables for dropdown visibility
//    // Variables for dropdown visibility
//    var dayMenuVisible by remember {mutableStateOf(false)}
//    var monthMenuVisible by remember {mutableStateOf(false)}
//
//
//    var buttonClicked by remember { mutableStateOf(false) }
//    var datePickerClicked by remember { mutableStateOf(false)}
//    var dayPickerClicked by remember { mutableStateOf(false) }
//    var monthPickerClicked by remember { mutableStateOf(false) }
//
//    val firstNameError = remember { mutableStateOf(false) }
//    val lastNameError = remember { mutableStateOf(false) }
//    val fellowshipError = remember { mutableStateOf(false) }
//    val phoneNumberError = remember { mutableStateOf(false) }
//    val datePickerError = remember { mutableStateOf(false) }
//
//    var errorMessage by remember { mutableStateOf("") }
//
//    selectedDayOfBirth.value = viewModel.birthDate.value
//    selectedMonthOfBirth.value = viewModel.birthMonth.value
//
//    Column(
//        Modifier.fillMaxWidth().padding(top = 64.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//    ) {
//
//        // Add TopAppBar with title
//        TopAppBar(
//            title = {
//                Column(
//                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 12.dp) // Add margin to create space below the title
//                ) {
//                    Text("Update user details...", color = Color.White) // Set text color to white
//                }
//            },
//            backgroundColor = Color(0xFF3F51B5) // Indigo color value
//        )
//        OutlinedTextField(
//            value = viewModel.firstName.value,
//            onValueChange = { viewModel.firstName.value = it },
//            label = { Text("First Name") },
//            isError = firstNameError.value, // Check if it's empty
//            singleLine = true, // Set singleLine to improve UI for mandatory fields
//            // Add styling to indicate error in red
//            textStyle = if (firstNameError.value) TextStyle(color = Color.Red) else LocalTextStyle.current
//        )
//        OutlinedTextField(
//            value = viewModel.middleName.value,
//            onValueChange = { viewModel.middleName.value = it },
//            label = { Text("Middle Name") },
//            //isError = viewModel.firstName.value.isBlank(),
//            singleLine = true // Set singleLine to improv
//        )
//        OutlinedTextField(
//            value = viewModel.lastName.value,
//            onValueChange = { viewModel.lastName.value = it },
//            label = { Text("Last Name") },
//            isError = lastNameError.value,//buttonClicked && viewModel.lastName.value.isBlank(),
//            singleLine = true,
//            textStyle = if (lastNameError.value) TextStyle(color = Color.Red) else LocalTextStyle.current
//
//        )
//
//        OutlinedTextField(
//            value = viewModel.phoneNumber.value,
//            onValueChange = { viewModel.phoneNumber.value = it },
//            label = { Text("Phone Number") },
//            isError = phoneNumberError.value || !viewModel.isPhoneNumberValid(viewModel.phoneNumber.value),
//            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//            singleLine = true,
//            textStyle = if (phoneNumberError.value || !viewModel.isPhoneNumberValid(viewModel.phoneNumber.value)) TextStyle(color = Color.Red) else LocalTextStyle.current
//
//            //visualTransformation = PasswordVisualTransformation()
//        )
//
//        OutlinedTextField(
//            value = viewModel.fellowship.value,
//            onValueChange = { viewModel.fellowship.value = it },
//            label = { Text("Fellowship") },
//            isError = fellowshipError.value,//buttonClicked && viewModel.fellowship.value.isBlank(),
//            singleLine = true,
//            textStyle = if (fellowshipError.value) TextStyle(color = Color.Red) else LocalTextStyle.current
//
//        )
//
//        // Date Joined Button and Text
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(vertical = 8.dp)
//        ) {
//            Button(
//                onClick = { mDatePickerDialog.show()
//                    datePickerClicked = true
//                },
//                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58))
//            ) {
//                Text(text = buttonText, color = Color.White)
//            }
//            Text(
//                text = formattedDate.value,
//                fontSize = 14.sp,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(start = 8.dp)
//            )
//        }
//
//        // Dropdown Menus
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text(
//                fontSize = 11.sp,
//                text ="Birth date and month: "
//            )
//            Box {
//                OutlinedButton(
//                    onClick = {
//                        dayPickerClicked = true
//                        dayMenuVisible = true },
//                    modifier = Modifier.padding(end = 8.dp)
//                ) {
//                    Text(selectedDayOfBirth.value ?: "Day")
//                }
//                DropdownMenu(
//                    expanded = dayMenuVisible,
//                    onDismissRequest = { dayMenuVisible = false },
//                    modifier = Modifier
//                        .width(100.dp) // Adjust the width here
//                        .height(200.dp) // Adjust the height here
//                ) {
//                    dayOfBirthOptions.forEach { day ->
//                        DropdownMenuItem(
//                            onClick = {
//                                selectedDayOfBirth.value = day
//                                dayMenuVisible = false
//                                viewModel.birthDate.value = day
//                            }
//                        ) {
//                            Text(text = day)
//                        }
//                    }
//                }
//            }
//            Box {
//                OutlinedButton(
//                    onClick = {
//
//                        monthPickerClicked = true
//                        monthMenuVisible = true
//                    }
//                ) {
//                    Text(selectedMonthOfBirth.value ?: "Month")
//                }
//                DropdownMenu(
//                    expanded = monthMenuVisible,
//                    onDismissRequest = { monthMenuVisible = false },
//                    modifier = Modifier
//                        .width(100.dp) // Adjust the width here
//                        .height(200.dp) // Adjust the height here
//                ) {
//                    monthOfBirthOptions.forEach { month ->
//                        DropdownMenuItem(
//                            onClick = {
//                                selectedMonthOfBirth.value = month
//                                monthMenuVisible = false
//                                viewModel.birthMonth.value = month
//                            }
//                        ) {
//                            Text(text = month)
//                        }
//                    }
//                }
//            }
//        }
//
//        if (errorMessage.isNotEmpty()) {
//            Text(
//                text = errorMessage,
//                color = Color.Red,
//                modifier = Modifier.padding(bottom = 8.dp)
//            )
//        }
//
//
//        Button(
//            onClick = {
//                // Check if all mandatory fields are filled
//                if (viewModel.firstName.value.isBlank()) {
//                    firstNameError.value = true
//                } else {
//                    firstNameError.value = false
//                }
//
//                if (viewModel.lastName.value.isBlank()) {
//                    lastNameError.value = true
//                } else {
//                    lastNameError.value = false
//                }
//
//                if (viewModel.fellowship.value.isBlank()) {
//                    fellowshipError.value = true
//                } else {
//                    fellowshipError.value = false
//                }
//
//                if (viewModel.phoneNumber.value.isBlank()) {
//                    phoneNumberError.value = true
//                } else {
//                    phoneNumberError.value = false
//                }
//                // Similarly, check and set error states for other fields
//
//
//
//                if (viewModel.firstName.value.isBlank() ||
//                    viewModel.lastName.value.isBlank() ||
//                    viewModel.phoneNumber.value.isBlank()
//
//
//
//                ) {
//
//                    // Update the errorMessage with the error message
//                    errorMessage = "Please fill all mandatory fields."
//
//                } else {
//                    // Perform sign-in logic here
//                    viewModel.performUpdate()
//                }
//            },
//            modifier = Modifier.fillMaxWidth().
//            padding(top = 16.dp).
//            padding(horizontal = 40.dp)
//        ) {
//            Text("Update member details")
//        }
//
//
//
//        viewModel.dateJoined.value = formattedDate.value;
//       // viewModel.birthMonth.value = selectedMonthOfBirth.value
//       // viewModel.birthDate.value = selectedDayOfBirth.value
//
//        if (submissionStatus == UpdateUserViewModel.SubmissionStatus.SUCCESS) {
//            AlertDialog(
//                onDismissRequest = { viewModel.submissionStatus.value = null },
//                title = { Text("Success") },
//                text = { Text(submissionMessage ?: "") },
//                confirmButton = {
//                    Button(onClick = { viewModel.submissionStatus.value = null }) {
//                        Text("OK")
//                    }
//                }
//            )
//        } else if (submissionStatus == UpdateUserViewModel.SubmissionStatus.ERROR) {
//            AlertDialog(
//                onDismissRequest = { viewModel.submissionStatus.value = null },
//                title = { Text("Error") },
//                text = { Text(submissionMessage ?: "") },
//                confirmButton = {
//                    Button(onClick = { viewModel.submissionStatus.value = null }) {
//                        Text("OK")
//                    }
//                }
//            )
//        }
//    }









    //********************************************************













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
            formattedDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
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
    var dayMenuVisible by remember { mutableStateOf(false) }
    var monthMenuVisible by remember { mutableStateOf(false) }


    var buttonClicked by remember { mutableStateOf(false) }
    var datePickerClicked by remember { mutableStateOf(false) }
    var dayPickerClicked by remember { mutableStateOf(false) }
    var monthPickerClicked by remember { mutableStateOf(false) }

    val firstNameError = remember { mutableStateOf(false) }
    val fieldOfStudyError = remember { mutableStateOf(false) }
    val lastNameError = remember { mutableStateOf(false) }
    val fellowshipError = remember { mutableStateOf(false) }
    val phoneNumberError = remember { mutableStateOf(false) }
    val welfareBalError = remember { mutableStateOf(false) }
    val twentyBalError = remember { mutableStateOf(false) }
    val occupationError = remember { mutableStateOf(false) }
    val datePickerError = remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }


    val fieldOfStudyoptions = listOf("Nurse", "Architecture", "IT","Business Managemenet",
        "Community Health","Accounting","Law","Human Resource","Psychology","")
    val fellowshipOptions = listOf("Jericho", "Jerusalem")
    var isDropdownVisible by remember { mutableStateOf(false) }
    var isTextFieldFocused by remember { mutableStateOf(false) }

    var selectedFieldOption by remember { mutableStateOf(fieldOfStudyoptions.first()) }
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    var textFieldBounds by remember { mutableStateOf<Rect?>(null) }

    var fostudy_expanded by remember { mutableStateOf(false) }
    var fellowship_expanded by remember { mutableStateOf(false) }

    val focusRequester = FocusRequester()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp, bottom = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        item{
            OutlinedTextField(
                value = viewModel.firstName.value,
                onValueChange = { viewModel.firstName.value = it },
                label = { Text("First Name") },
                isError = firstNameError.value, // Check if it's empty
                singleLine = true, // Set singleLine to improve UI for mandatory fields
                // Add styling to indicate error in red
                textStyle = if (firstNameError.value) TextStyle(color = Color.Red) else LocalTextStyle.current
            )}
        item{
            OutlinedTextField(
                value = viewModel.middleName.value,
                onValueChange = { viewModel.middleName.value = it },
                label = { Text("Middle Name") },
                //isError = viewModel.firstName.value.isBlank(),
                singleLine = true // Set singleLine to improv
            )}
        item{
            OutlinedTextField(
                value = viewModel.lastName.value,
                onValueChange = { viewModel.lastName.value = it },
                label = { Text("Last Name") },
                isError = lastNameError.value,//buttonClicked && viewModel.lastName.value.isBlank(),
                singleLine = true,
                textStyle = if (lastNameError.value) TextStyle(color = Color.Red) else LocalTextStyle.current

            )}
        item{
            OutlinedTextField(
                value = viewModel.phoneNumber.value,
                onValueChange = { viewModel.phoneNumber.value = it },
                label = { Text("Phone Number") },
                isError = phoneNumberError.value || !viewModel.isPhoneNumberValid(viewModel.phoneNumber.value),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = if (phoneNumberError.value || !viewModel.isPhoneNumberValid(viewModel.phoneNumber.value)) TextStyle(
                    color = Color.Red
                ) else LocalTextStyle.current

                //visualTransformation = PasswordVisualTransformation()
            )}

        item {
            OutlinedTextField(
                value = viewModel.openingWelBal.value,
                onValueChange = { viewModel.openingWelBal.value = it },
                label = { Text("Welfare Bal") },
                isError = welfareBalError.value,//buttonClicked && viewModel.lastName.value.isBlank(),
                singleLine = true,
                textStyle = if (welfareBalError.value) TextStyle(color = Color.Red) else LocalTextStyle.current

            )
        }

        item {
            OutlinedTextField(
                value = viewModel.openingTwentyBal.value,
                onValueChange = { viewModel.openingTwentyBal.value = it },
                label = { Text("Twenty Bal") },
                isError = twentyBalError.value,//buttonClicked && viewModel.lastName.value.isBlank(),
                singleLine = true,
                textStyle = if (twentyBalError.value) TextStyle(color = Color.Red) else LocalTextStyle.current

            )
        }
//        item {
//        OutlinedTextField(
//            value = viewModel.fellowship.value,
//            onValueChange = { viewModel.fellowship.value = it },
//            label = { Text("Fellowship") },
//            isError = fellowshipError.value,//buttonClicked && viewModel.fellowship.value.isBlank(),
//            singleLine = true,
//            textStyle = if (fellowshipError.value) TextStyle(color = Color.Red) else LocalTextStyle.current
//
//        )}

        item {
//        OutlinedTextField(
//            value = viewModel.fellowship.value,
//            onValueChange = { viewModel.fellowship.value = it },
//            label = { Text("Field of study") },
//            isError = fellowshipError.value,
//            singleLine = true,
//            textStyle = if (fellowshipError.value) TextStyle(color = Color.Red) else LocalTextStyle.current,
//            modifier = Modifier.focusRequester(focusRequester)
//        )
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//            modifier = Modifier.width(TextFieldDefaults.MinWidth)
//        ) {
//            // Add your selectable options here
//
//            fieldOfStudyptions.forEach { option ->
//                DropdownMenuItem(onClick = {
//                    viewModel.fellowship.value = option
//                    expanded = false // Close the dropdown after selection
//                }) {
//                    Text(option)
//                }
//        }}

            OutlinedTextField(
                value = viewModel.fellowship.value,
                onValueChange = { viewModel.fellowship.value = it },
                label = { Text("Fellowship") },
                isError = fellowshipError.value,
                singleLine = true,
                textStyle = if (fellowshipError.value) TextStyle(color = Color.Red) else LocalTextStyle.current,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        fellowship_expanded = focusState.isFocused
                    }

            )
            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
                //fellowship_expanded=true
            }

            DropdownMenu(
                expanded = fellowship_expanded,
                onDismissRequest = { fellowship_expanded= false },
                modifier = Modifier.width(TextFieldDefaults.MinWidth)
            ) {
                // Add your selectable options here

                fellowshipOptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        viewModel.fellowship.value = option
                        fellowship_expanded = false
                        // Close the dropdown after selection
                    }) {
                        Text(option)
                    }
                }
            }

            OutlinedTextField(
                value = viewModel.occupation.value,
                onValueChange = { viewModel.occupation.value = it },
                label = { Text("Occupation") },
                isError = occupationError.value,//buttonClicked && viewModel.lastName.value.isBlank(),
                singleLine = true,
                textStyle = if (occupationError.value) TextStyle(color = Color.Red) else LocalTextStyle.current

            )

            OutlinedTextField(
                value = viewModel.fieldOfStudy.value,
                onValueChange = { viewModel.fieldOfStudy.value = it },
                label = { Text("Field of study") },
                isError = fieldOfStudyError.value,
                singleLine = true,
                textStyle = if (fellowshipError.value) TextStyle(color = Color.Red) else LocalTextStyle.current,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        fostudy_expanded = focusState.isFocused
                    }
            )
            LaunchedEffect(focusRequester) {
                focusRequester.requestFocus()
                //fostudy_expanded=true
            }

            DropdownMenu(
                expanded = fostudy_expanded,
                onDismissRequest = { fostudy_expanded = false },
                modifier = Modifier.width(TextFieldDefaults.MinWidth)
            ) {
                // Add your selectable options here

                fieldOfStudyoptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        viewModel.fieldOfStudy.value = option
                        fostudy_expanded = false // Close the dropdown after selection
                    }) {
                        Text(option)
                    }
                }
            }
        }

        // Date Joined Button and Text
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                        mDatePickerDialog.show()
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
        }

        // Dropdown Menus
        item{
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    fontSize = 11.sp,
                    text = "Birth date and month: "
                )
                Box {
                    OutlinedButton(
                        onClick = {
                            dayPickerClicked = true
                            dayMenuVisible = true
                        },
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

                    if (viewModel.openingWelBal.value.isBlank()) {
                        welfareBalError.value = true
                    } else {
                        welfareBalError.value = false
                    }

                    if (viewModel.openingTwentyBal.value.isBlank()) {
                        twentyBalError.value = true
                    } else {
                        twentyBalError.value = false
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
                    if (viewModel.fieldOfStudy.value.isBlank()) {
                        fieldOfStudyError.value = true
                    } else {
                        fieldOfStudyError.value = false
                    }


                    if (viewModel.firstName.value.isBlank() ||
                        viewModel.lastName.value.isBlank() ||
                        viewModel.phoneNumber.value.isBlank() ||
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
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp)
//                    .background(color = Color(0xFF196F3D))
                    .padding(horizontal = 40.dp)

            ) {
                Text("Update Member")
            }



            viewModel.dateJoined.value = formattedDate.value;
            viewModel.birthMonth.value = selectedMonthOfBirth.value
            viewModel.birthDate.value = selectedDayOfBirth.value
            // viewModel.fieldOfStudy.value = fieldOfStudy.value

            if (submissionStatus == UpdateUserViewModel.SubmissionStatus.SUCCESS) {
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
        }}

//    // Expand the dropdown when the field gains focus
//    LaunchedEffect(focusRequester) {
//        focusRequester.requestFocus()
//        expanded = true
//    }

    // Expand the dropdown when the field gains focus
}
