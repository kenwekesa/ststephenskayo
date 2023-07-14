package com.ack.ststephenskayo
import android.content.Context
import android.content.SharedPreferences
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
import java.io.FileOutputStream

data class Paym(val date: String, val name: String, val amount: String)

class MemberPaymentViewModel(private val phoneNumber: String, private val password: String) : ViewModel() {    val payments = mutableStateListOf<Paym>()


    init {
        fetchPayments()
    }

    private fun fetchPayments() {
        val db = FirebaseFirestore.getInstance()
        val paymentsCollection = db.collection("payments")

        viewModelScope.launch {
            try {
                val querySnapshot = withContext(Dispatchers.IO) {
                    paymentsCollection.whereEqualTo("phoneNumber", phoneNumber)
                        .get()
                        .await()
                }

                val paymentList = querySnapshot.documents.mapNotNull { document ->
                    val date = document.getString("date")
                    val name = document.getString("phoneNumber")
                    val amount = document.getString("amount")

                    if (date != null && name != null && amount != null) {
                        Paym(date, name, amount)
                    } else {
                        null
                    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve user details
        sharedPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val phoneNumber = sharedPrefs.getString("phoneNumber", "") ?: ""
        val password = sharedPrefs.getString("password", "") ?: ""
        setContent {
            MemberPaymentScreen(phoneNumber, password)
        }
    }
}

@Composable
fun MemberPaymentScreen(phoneNumber:String,password:String) {
    //val viewModel = viewModel<MemberPaymentViewModel>()
   // val viewModel = viewModel<MemberPaymentViewModel>(factory = ViewModelFactory(phoneNumber, password))

//    val viewModelFactory = remember { MemberPaymentViewModelFactory(phoneNumber, password) }
//    val viewModel: MemberPaymentViewModel = viewModel(viewModelProviderFactory = viewModelFactory)
        //val viewModel = viewModel(factory = viewModelFactory)

    val viewModelFactory = remember { MemberPaymentViewModelFactory(phoneNumber, password) }
    val viewModel: MemberPaymentViewModel = viewModel(factory = viewModelFactory)


    MemberPaymentView(viewModel = viewModel,phoneNumber,password)
}

class MemberPaymentViewModelFactory(
    private val phoneNumber: String,
    private val password: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberPaymentViewModel::class.java)) {
            return MemberPaymentViewModel(phoneNumber, password) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


fun generateExcelSheet(payments: List<Paym>) {
    val rootDirectory = Environment.getExternalStorageDirectory().toString()
    val stStephensDirectory = File("$rootDirectory/StStephens")


    val fileName = "example.xls"
    val filePath = Environment.getExternalStorageDirectory().toString() + File.separator +"StStephens"+File.separator + fileName

    val writableWorkbook: WritableWorkbook = Workbook.createWorkbook(File(filePath))
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
}
@Composable
fun MemberPaymentView(viewModel: MemberPaymentViewModel,phoneNumber: String,password: String) {
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
                Text(text = "Name")
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

            // Generate Excel button
            Button(
                onClick = { generateExcelSheet(viewModel.payments) },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "Generate Excel")
            }
        }
    }
}
