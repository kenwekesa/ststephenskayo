package com.ack.ststephenskayo
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import android.widget.EditText
//import androidx.appcompat.app.AppCompatActivity
//import com.ack.ststephenskayo.databinding.ActivityLoginBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
//import com.google.firebase.auth.FirebaseAuthInvalidUserException
//
//class LoginActivity : AppCompatActivity() {
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//        setTitle("")
//
//
//        // Initialize Firebase Authentication
//        auth = FirebaseAuth.getInstance()
//
//        findViewById<Button>(R.id.login_btn).setOnClickListener {
//            val username = findViewById<EditText>(R.id.username).text.toString().trim()
//            val password = findViewById<EditText>(R.id.password).text.toString()
//
//            if (username.isNotEmpty() && password.isNotEmpty()) {
//                // Call the login function
//                login(username, password)
//            } else {
//                // Username or password is empty, display an error message
//                // TODO: Handle empty username or password
//            }
//        }
//    }
//
//    private fun login(username: String, password: String) {
//        auth.signInWithEmailAndPassword(username, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Login successful, navigate to the main activity or perform desired actions
//                    val user = auth.currentUser
//
//                    val intent = Intent(this, MembersActivity::class.java);
//                    startActivity(intent)
//
//                    // TODO: Handle successful login
//                } else {
//                    // Login error, display an error message or perform error handling
//                    val exception = task.exception
//                    when (exception) {
//                        is FirebaseAuthInvalidUserException -> {
//                            // Invalid user email
//                            // TODO: Handle invalid user
//                        }
//                        is FirebaseAuthInvalidCredentialsException -> {
//                            // Invalid user password
//                            // TODO: Handle invalid password
//                        }
//                        else -> {
//                            // Other login error
//                            // TODO: Handle other login error
//                        }
//                    }
//                }
//            }
//    }
//}

//---------------------------------------------------------------
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData

import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setTitle("User login")
        setContent {
            LoginView(context= this)
        }
    }
}

class LoginViewModel : ViewModel() {
    val phoneNumber = mutableStateOf("")
    val password = mutableStateOf("")
    val loginStatus = MutableLiveData<LoginStatus>()

    enum class LoginStatus {
        SUCCESS,
        ERROR
    }

    fun performLogin(context: Context) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users")
            .whereEqualTo("phoneNumber", phoneNumber.value)
            .whereEqualTo("password", password.value)

        userRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    loginStatus.value = LoginStatus.ERROR
                } else {
                    loginStatus.value = LoginStatus.SUCCESS
                    navigateToNextActivity(context)
                }
            }
            .addOnFailureListener {
                loginStatus.value = LoginStatus.ERROR
            }
    }

    private fun navigateToNextActivity(context: Context) {
        //val int = Intent()
        val intent = Intent(context, MembersActivity::class.java);
        context.startActivity(intent)
    }
}

@Composable
fun LoginView(context: Context,viewModel: LoginViewModel = viewModel()) {
    val loginStatus by viewModel.loginStatus.observeAsState()
    var passwordVisibility by remember { mutableStateOf(false) }

    var loginStatusMessage by remember { mutableStateOf("") }

    LaunchedEffect(loginStatus) {
        when (loginStatus) {
            LoginViewModel.LoginStatus.SUCCESS -> {
                loginStatusMessage = "Login successful!"
            }
            LoginViewModel.LoginStatus.ERROR -> {
                loginStatusMessage = "Invalid username or password"
            }
            else -> {
                loginStatusMessage = ""
            }
        }
    }

    Column(
        Modifier.fillMaxWidth().padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.phoneNumber.value = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            //visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            visualTransformation = PasswordVisualTransformation(),
//            visualTransformation = if (passwordVisibility) {
//                VisualTransformation.None
//            } else {
//                PasswordVisualTransformation()
//            },
//            trailingIcon = {
//                val icon = if (passwordVisibility) {
//                    Icons.Filled.Visibility
//                } else {
//                    Icons.Filled.VisibilityOff
//                }
//
//                val description = if (passwordVisibility) {
//                    "Hide password"
//                } else {
//                    "Show password"
//                }
//
//                Icon(
//                    imageVector = icon,
//                    contentDescription = description,
//                    modifier = Modifier
//                        .clickable { passwordVisibility = !passwordVisibility }
//                        .padding(end = 8.dp)
//                        .size(24.dp)
//                )
//            }
        )

        Button(
            onClick = { viewModel.performLogin(context) },
            modifier = Modifier.fillMaxWidth().
            padding(top = 16.dp).
            padding(horizontal = 18.dp)        ) {
            Text("Log In")
        }

        if (loginStatusMessage.isNotEmpty()) {
            Text(loginStatusMessage)
        }
    }

}
