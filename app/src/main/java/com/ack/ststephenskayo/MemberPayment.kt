package com.ack.ststephenskayo



import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.firebase.firestore.FirebaseFirestore
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

data class Paym(val date: String, val name: String, val amount: String)

class MemberPaymentViewModel(private val phoneNumber: String, private val password: String, payment_type: String) : ViewModel() {
    val payments = mutableStateListOf<Paym>()

    init {
        fetchPayments(payment_type)
    }

    private fun fetchPayments(payment_type: String) {
        val db = FirebaseFirestore.getInstance()

        val collection_name = when (payment_type) {
            "welfare" -> "welfare_payments"
            "twenty" -> "twenty_twenty_payments"
            else -> ""
        }

        val paymentsCollection = db.collection(collection_name)

        viewModelScope.launch {
            try {
                val querySnapshot = withContext(Dispatchers.IO) {
                    paymentsCollection.whereEqualTo("phoneNumber", phoneNumber)
                        .get()
                        .await()
                }

                val paymentList = querySnapshot.documents.flatMap { document ->

                    val paymentsArray = document.get("payments") as? ArrayList<HashMap<String, Any>>

                    paymentsArray?.mapNotNull { payment ->
                        val date = payment["date"] as? String
                        val amount = payment["amount"] as? String

                        if (date != null && amount != null) {
                            Paym(date, phoneNumber, amount)
                        } else {
                            null
                        }
                    } ?: emptyList()
                }

                payments.addAll(paymentList)
            } catch (e: Exception) {
                // Handle exception here
                Log.e("MemberPaymentViewModel", "Error fetching payments: ${e.message}", e)
            }
        }
    }

}

class MemberPayment : AppCompatActivity() {
    private lateinit var sharedPrefs: SharedPreferences

    private val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve user details
        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val phoneNumber = sharedPrefs.getString("phoneNumber", "") ?: ""
        val password = sharedPrefs.getString("password", "") ?: ""

        // Retrieve the extra from the Intent
        val payment_type = intent?.getStringExtra("payment_type")

        setContent {
            MemberPaymentScreen(phoneNumber, password, this@MemberPayment,payment_type as String)
        }

        checkWriteExternalStoragePermission()
    }

    private fun checkWriteExternalStoragePermission() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Prompt the user to grant permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val viewModel = ViewModelProvider(this).get(MemberPaymentViewModel::class.java)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Generate and save the Excel file
                generateExcelSheet(this,viewModel.payments)

            } else {
                // Permission denied
                // Handle the permission denial scenario
            }
        }
    }


}

@Composable
fun MemberPaymentScreen(phoneNumber: String, password: String,context: Context,payment_type: String) {
    val viewModelFactory = MemberPaymentViewModelFactory(phoneNumber, password, payment_type)
    val viewModel: MemberPaymentViewModel = viewModel(factory = viewModelFactory)

    MemberPaymentView(viewModel = viewModel, phoneNumber = phoneNumber, password = password,context)
}


class MemberPaymentViewModelFactory(
    private val phoneNumber: String,
    private val password: String,
    private  val payment_type: String,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberPaymentViewModel::class.java)) {
            return MemberPaymentViewModel(phoneNumber, password, payment_type) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun MemberPaymentView(viewModel: MemberPaymentViewModel,phoneNumber: String,password: String,context:Context) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Table header
            Row {
                Text(text = "Date")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Phone No.")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "Amount")
            }

            // Table rows
            for (payment in viewModel.payments) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = payment.date)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = phoneNumber)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = payment.amount)
                }
            }

            //Generate Excel button
            Button(
                onClick = { generateExcelSheet(context,viewModel.payments) },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "Generate Excel")
            }
        }
    }


}

// Function to generate and save the Excel file

// Function to generate and save the Excel file
fun generateExcelSheet(context: Context, payments: List<Paym>) {
    try {
        val stStephensDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "StStephens"
        )

        if (!stStephensDirectory.exists()) {
            stStephensDirectory.mkdirs()
        }

        val fileName = "example.xls"
        val filePath = File(stStephensDirectory, fileName)

        if (!filePath.exists()) {
            filePath.createNewFile()
        }

        val writableWorkbook: WritableWorkbook = Workbook.createWorkbook(filePath)
        val sheet: WritableSheet = writableWorkbook.createSheet("Payments", 0)

        // Create header row
        val headerRow: Label = Label(0, 0, "Date")
        sheet.addCell(headerRow)
        headerRow.setString("Name")
        headerRow.setString("Amount")

        // Create data rows
        var rowIndex = 1
        for (payment in payments) {
            val dataRow: Label = Label(0, rowIndex, payment.date)
            sheet.addCell(dataRow)
            dataRow.setString(payment.name)
            dataRow.setString(payment.amount)
            rowIndex++
        }

        // Write and close the workbook
        writableWorkbook.write()
        writableWorkbook.close()
    } catch (e: Exception) {
        // Handle the exception
        Log.e("Error", "Error generating Excel file: ${e.message}", e)
    }
}
//fun generateExcelSheet(payments: List<Paym>) {
//    try {
//
//
//        val rootDirectory =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        // val rootDirectory = Environment.getExternalStorageDirectory()
//        val stStephensDirectory = File(rootDirectory, "StStephens")
//
//        // Get the root directory
//
//
//      if (!stStephensDirectory.exists()) {
//          stStephensDirectory.mkdirs()
//      }
//
//
//        val fileName = "example.xls"
//        val filePath = File(stStephensDirectory, fileName)
//
//        if (!filePath.exists()) {
//            filePath.createNewFile()
//        }
//        val writableWorkbook: WritableWorkbook = Workbook.createWorkbook(File(filePath.toString()))
//        val sheet: WritableSheet = writableWorkbook.createSheet("Payments", 0)
//
//        // Create header row
//        val headerRow: Label = Label(0, 0, "Date")
//        sheet.addCell(headerRow)
//        headerRow.setString("Name")
//        headerRow.setString("Amount")
//
//        // Create data rows
//        var rowIndex = 1
//        for (payment in payments) {
//            val dataRow: Label = Label(0, rowIndex, payment.date)
//            sheet.addCell(dataRow)
//            dataRow.setString(payment.name)
//            dataRow.setString(payment.amount)
//            rowIndex++
//        }
//
//        // Write and close the workbook
//        writableWorkbook.write()
//        writableWorkbook.close()
//    } catch (e: Exception) {
//        // Handle the exception
//        Log.e("Error", " ------error", e)
//
//    }
//}

// Rest of the code remains the same