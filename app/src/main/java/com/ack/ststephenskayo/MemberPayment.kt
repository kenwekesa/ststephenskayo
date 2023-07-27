package com.ack.ststephenskayo

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
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
import java.io.OutputStream
import java.util.UUID

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
            MemberPaymentScreen(phoneNumber, password, this@MemberPayment, payment_type as String)
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

    @RequiresApi(Build.VERSION_CODES.Q)
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
                generateExcelSheet(this, viewModel.payments)

            } else {
                // Permission denied
                // Handle the permission denial scenario
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MemberPaymentScreen(phoneNumber: String, password: String, context: Context, payment_type: String) {
    val viewModelFactory = MemberPaymentViewModelFactory(phoneNumber, password, payment_type)
    val viewModel: MemberPaymentViewModel = viewModel(factory = viewModelFactory)

    MemberPaymentView(viewModel = viewModel, phoneNumber = phoneNumber, password = password, context = context)
}

class MemberPaymentViewModelFactory(
    private val phoneNumber: String,
    private val password: String,
    private val payment_type: String,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberPaymentViewModel::class.java)) {
            return MemberPaymentViewModel(phoneNumber, password, payment_type) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MemberPaymentView(viewModel: MemberPaymentViewModel, phoneNumber: String, password: String, context: Context) {
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

            // Generate Excel button
            Button(
                onClick = { generateExcelSheet(context, viewModel.payments) },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "Generate Excel")
            }

            // Generate Excel button
            Button(
                onClick = { generatePdf(context, viewModel.payments, "WELFARE PAYMENT  STATEMENT") },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "Generate pdf")
            }
        }
    }
}
// Function to show Snackbar with 'Open' button
private fun showSnackbarWithOpenButton(context: Context, fileUri: Uri) {
    val parentView = (context as Activity).findViewById<View>(android.R.id.content)

    val snackbar = Snackbar.make(parentView, "PDF file saved successfully!", Snackbar.LENGTH_INDEFINITE)
    snackbar.setAction("Open") {
        openPdfFile(context, fileUri)
    }
    snackbar.show()
}

// Function to open the PDF file with an intent
private fun openPdfFile(context: Context, fileUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(fileUri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No PDF viewer app found", Toast.LENGTH_SHORT).show()
    }
}
// Function to generate and save the Excel file
//fun generateExcelSheet(context: Context, payments: List<Paym>) {
//    try {
//        val stStephensDirectory = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//            "StStephens"
//        )
//
//        if (!stStephensDirectory.exists()) {
//            stStephensDirectory.mkdirs()
//        }
//
//        val fileName = "example.xls"
//        val filePath = File(stStephensDirectory, fileName)
//
//        if (!filePath.exists()) {
//            filePath.createNewFile()
//        }
//
//        val writableWorkbook: WritableWorkbook = Workbook.createWorkbook(filePath)
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
//        Log.e("Error", "Error generating Excel file: ${e.message}", e)
//    }
//}


// Function to generate and save the Excel file
@RequiresApi(Build.VERSION_CODES.Q)
fun generateExcelSheet(context: Context, payments: List<Paym>) {
    try {

        val uniqueId = UUID.randomUUID().toString()
        val fileName = "${payments[0].name}_$uniqueId.xls"

        // Create a ContentValues object to store the file metadata
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/StStephens")
        }

        // Get the content resolver to insert the file
        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { uri ->
            // Open an output stream using the uri
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                val writableWorkbook: WritableWorkbook = Workbook.createWorkbook(outputStream)
                val sheet: WritableSheet = writableWorkbook.createSheet("Payments", 0)

                // Create header row
                // Create header row
                val headerDate: Label = Label(0, 0, "Date")
                val headerName: Label = Label(1, 0, "Name")
                val headerAmount: Label = Label(2, 0, "Amount")

// Add the header labels to the sheet
                sheet.addCell(headerDate)
                sheet.addCell(headerName)
                sheet.addCell(headerAmount)


                // Create data rows
                var rowIndex = 1
                for (payment in payments) {
                    val dateCell: Label = Label(0, rowIndex, payment.date)
                    val nameCell: Label = Label(1, rowIndex, payment.name) // Add the "Name" data to column 1
                    val amountCell: Label = Label(2, rowIndex, payment.amount) // Add the "Amount" data to column 2

                    sheet.addCell(dateCell)
                    sheet.addCell(nameCell)
                    sheet.addCell(amountCell)

                    rowIndex++
                }

                // Write and close the workbook
                writableWorkbook.write()
                writableWorkbook.close()

                // Close the output stream
                outputStream.close()

                Toast.makeText(context, "Excel file saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // Handle error opening output stream
                Log.e("Error", "Error opening output stream")
                Toast.makeText(context, "Failed to save Excel file!", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            // Handle error creating URI
            Log.e("Error", "Error creating URI")
            Toast.makeText(context, "Failed to create file!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        // Handle the exception
        Log.e("Error", "Error generating Excel file: ${e.message}", e)
        Toast.makeText(context, "Error generating Excel file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}



@RequiresApi(Build.VERSION_CODES.Q)
fun generatePdf(context: Context, payments: List<Paym>, title: String) {
    val fileName = "${payments[0].name}.pdf"

    // Create the document
    val document = Document()
    try {
        // Create the PDF file in the StStephens directory
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/StStephens")
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { uri ->
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                // Set up the PDF writer
                PdfWriter.getInstance(document, outputStream)

                // Open the document
                document.open()

                // Add the title to the document
                val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f, Font.UNDERLINE)
                val titleParagraph = Paragraph(title, titleFont)
                titleParagraph.alignment = Paragraph.ALIGN_CENTER
                titleParagraph.spacingAfter = 10f
                document.add(titleParagraph)

                // Create the table with 3 columns
                val table = PdfPTable(3)

                // Set table properties
                table.widthPercentage = 100f
                table.setHeaderRows(1) // The first row will be treated as the header

                // Set table border color
                table.defaultCell.borderColor = BaseColor.WHITE

                // Define the heading row background color
                val headingBackgroundColor = BaseColor.GRAY

                // Add table headers with custom style
                addCellWithCustomStyle(table, "Date", headingBackgroundColor)
                addCellWithCustomStyle(table, "Name", headingBackgroundColor)
                addCellWithCustomStyle(table, "Amount", headingBackgroundColor)

                // Define the data row background color
                val dataBackgroundColor = BaseColor.LIGHT_GRAY

                // Add data rows with custom style
                for (payment in payments) {
                    addCellWithCustomStyle(table, payment.date, dataBackgroundColor)
                    addCellWithCustomStyle(table, payment.name, dataBackgroundColor)
                    addCellWithCustomStyle(table, payment.amount, dataBackgroundColor)
                }

                // Add the table to the document
                document.add(table)

                // Close the document
                document.close()

                // Close the output stream
                outputStream.close()

                // Show the Snackbar with the 'Open' button
                showSnackbarWithOpenButton(context, uri)
                //Toast.makeText(context, "PDF file saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // Handle error opening output stream
                Log.e("Error", "Error opening output stream")
                Toast.makeText(context, "Failed to save PDF file!", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            // Handle error creating URI
            Log.e("Error", "Error creating URI")
            Toast.makeText(context, "Failed to create file!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        // Handle the exception
        Log.e("Error", "Error generating PDF file: ${e.message}", e)
        Toast.makeText(context, "Error generating PDF file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// Function to add cell with custom style
private fun addCellWithCustomStyle(table: PdfPTable, text: String, bgColor: BaseColor) {
    val cell = PdfPCell(Phrase(text))
    cell.backgroundColor = bgColor
    cell.borderColor = BaseColor.WHITE
    cell.setPadding(5f)
    table.addCell(cell)
}


//@RequiresApi(Build.VERSION_CODES.Q)
//fun generatePdf(context: Context, payments: List<Paym>) {
//    val fileName = "${payments[0].name}.pdf"
//
//    // Create the document
//    val document = Document()
//    try {
//        // Create the PDF file in the StStephens directory
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/StStephens")
//        }
//
//        val contentResolver = context.contentResolver
//        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
//
//        uri?.let { uri ->
//            val outputStream = contentResolver.openOutputStream(uri)
//            if (outputStream != null) {
//                // Set up the PDF writer
//                PdfWriter.getInstance(document, outputStream)
//
//                // Open the document
//                document.open()
//
//                // Create the table with 3 columns
//                val table = PdfPTable(3)
//
//                // Add table headers
//                table.addCell("Date")
//                table.addCell("Name")
//                table.addCell("Amount")
//
//                // Add data rows
//                for (payment in payments) {
//                    table.addCell(payment.date)
//                    table.addCell(payment.name)
//                    table.addCell(payment.amount)
//                }
//
//                // Add the table to the document
//                document.add(table)
//
//                // Close the document
//                document.close()
//
//                // Close the output stream
//                outputStream.close()
//
//                Toast.makeText(context, "PDF file saved successfully!", Toast.LENGTH_SHORT).show()
//            } else {
//                // Handle error opening output stream
//                Log.e("Error", "Error opening output stream")
//                Toast.makeText(context, "Failed to save PDF file!", Toast.LENGTH_SHORT).show()
//            }
//        } ?: run {
//            // Handle error creating URI
//            Log.e("Error", "Error creating URI")
//            Toast.makeText(context, "Failed to create file!", Toast.LENGTH_SHORT).show()
//        }
//    } catch (e: Exception) {
//        // Handle the exception
//        Log.e("Error", "Error generating PDF file: ${e.message}", e)
//        Toast.makeText(context, "Error generating PDF file: ${e.message}", Toast.LENGTH_SHORT).show()
//    }
//}
