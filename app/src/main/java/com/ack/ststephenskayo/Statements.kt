package com.ack.ststephenskayo

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import jxl.CellView
import jxl.Workbook
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.format.VerticalAlignment
import jxl.write.Label
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import jxl.write.WritableWorkbook
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID


class Statements : AppCompatActivity() {

    private val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1

    val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statements)
        checkWriteExternalStoragePermission()

        val expandableListView = findViewById<ExpandableListView>(R.id.expandableListView)

        // Replace the sample data with your actual data
        val groupList = listOf("Welfare Statements", "20-20 Statements")
        val childList = mapOf(
            "Welfare Statements" to listOf("Members balance report pdf", "Members balance report excel"),
            "20-20 Statements" to listOf("Members balance report pdf", "Members balance report excel")
        )

        val adapter = CustomExpandableListAdapter(this, groupList, childList)
        expandableListView.setAdapter(adapter)

        // Set the OnChildClickListener to the ExpandableListView
        expandableListView.setOnChildClickListener { parent, view, groupPosition, childPosition, id ->
            // Get the text of the clicked child item
            val clickedChild = childList[groupList[groupPosition]]?.get(childPosition)
            val groupclicked = groupList[groupPosition]



            // Perform actions based on the clicked child item
            // For example, you can open the document associated with the clicked child item
            // or show a message with the child item text
            if(groupclicked == "Welfare Statements")
            {
                if(clickedChild!= null && clickedChild.contains("pdf"))
                {

                    retrieveUserData("P","Welfare")
                }
                else
                {

                    retrieveUserData("E", "Welfare")
                }

            }
            else if(groupclicked == "20-20 Statements")
            {

                if(clickedChild!= null && clickedChild.contains("pdf"))
                {
                    try {

                        retrieveUserData("P", "Twenty")
                    }
                    catch (e:Exception)
                    {
                        Log.d("User retrieval",e.message as String)
                    }
                }
                else
                {
                    try {
                       // Toast.makeText(this, "Clicked: - $groupclicked - $clickedChild", Toast.LENGTH_SHORT).show()
                        retrieveUserData("E", "Twenty")
                    }
                    catch (e:Exception)
                    {
                        Log.d("User retrieval",e.message as String)
                    }
                }

            }
            //Toast.makeText(this, "Clicked: - $groupclicked - $clickedChild", Toast.LENGTH_SHORT).show()

            // Return true to indicate that the click event has been handled
            true
        }
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun retrieveUserData(report_type: String, report_category: String) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userDetailsList: MutableList<Map<String, Any?>> = mutableListOf()
                    if (task.result?.isEmpty == true) {
                        // Handle the case when the QuerySnapshot is empty (no documents found)
                        Log.d("Firestore", "No documents found in the 'users' collection.")
                    } else {
                        var report_title = ""
                        for (document in task.result!!) {
                            val user = document.toObject(UserModel::class.java)


                            var some = report_category
                            if (user != null) {


                                var balance = 0.0
                                var totalPaid = 0.0
                                val dateFormatter = DateTimeFormatter.ofPattern("[d/M/yyyy][dd/MM/yyyy]", Locale.getDefault())
                                if(report_category == "Welfare") {

                                    report_title = "Welfare Report"
                                    totalPaid = user.total_welfare_paid
                                    balance = if (user.dateJoined?.isNotEmpty() == true) {
                                        ((ChronoUnit.MONTHS.between(
                                            LocalDate.parse(
                                                user.dateJoined,
                                                dateFormatter
                                            ), LocalDate.now()
                                        ) * 100).toDouble() - user.total_welfare_paid).toDouble()
                                    } else {
                                        0.0
                                    }
                                }
                                else if(report_category == "Twenty")
                                {
                                    report_title = "TWENTY-TWENTY STATEMENTS"
                                    totalPaid = user.total_twenty_paid
                                    balance = if (user.dateJoined?.isNotEmpty() == true) {
                                        ((ChronoUnit.WEEKS.between(
                                            LocalDate.parse(
                                                user.dateJoined,
                                                dateFormatter
                                            ), LocalDate.now()
                                        ) * 20).toDouble() - user.total_twenty_paid).toDouble()
                                    } else {
                                        0.0
                                    }
                                }
                                val tempObject = mapOf(
                                    "name" to "${user.firstName} ${user.lastName}",
                                    "dateJoined" to "${user.dateJoined}",
                                    "totalWelfare" to "${user.total_welfare_paid}",
                                    "totalTwenty" to "${user.total_twenty_paid}",
                                    "totalPaid" to "${totalPaid}",
                                    "balance" to balance,

                                    )
                                userDetailsList.add(tempObject)
                            }
                            // Rest of the code remains the same...
                            // ... (the data processing part)
                            // You can keep the existing code that processes the retrieved data
                            // and adds it to the userDetailsList.
                        }

                        // Call the appropriate report generation function
                        if (report_type == "P") {
                            generatePdfStatement(this@Statements, userDetailsList, report_title)
                        } else if (report_type == "E") {
                            generateExcelSheetStatement(this@Statements, userDetailsList, report_title)
                        }
                    }
                } else {
                    Log.e("Firestore Error", "Error getting documents: ", task.exception)
                }
            }
        } catch (e: Exception) {
            Log.e("Database retrieval", e.message, e)
        }
    }




    fun retrieveUserTwentyData(report_type: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                val users: MutableList<User> = mutableListOf()

                // Loop through all the children under "users" and parse them into User objects
                for (childSnapshot in dataSnapshot.children) {
                    val user = childSnapshot.getValue(User::class.java)
                    if (user != null) {
                        users.add(user)
                    }
                }

                // Now 'users' contains all the User objects retrieved from the database
                // You can use the data as needed, for example, passing it to your generateExcelSheetStatement or generatePdfStatement functions.
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if the retrieval is canceled
                Log.e("Database Error", "Error retrieving data: ${databaseError.message}")
            }
        })
    }
}










class CustomExpandableListAdapter(
    private val context: Context,
    private val groupList: List<String>,
    private val childList: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return groupList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return childList[groupList[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return groupList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return childList[groupList[groupPosition]]?.get(childPosition) ?: ""
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        val groupItem = getGroup(groupPosition)

            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.statements_group_item, null) // Set parent as null



        // Get the TextView for the group item
        val groupTextView = view.findViewById<TextView>(R.id.textMainMenuItem)

        // Set the text for the group item
        groupTextView.text = groupItem.toString()


        return view
    }


    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childText = getChild(groupPosition, childPosition) as String

        // Inflate the child view layout
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.report_item_list_child, null)

        // Customize the appearance of the child view here
        val childTextView = view.findViewById<TextView>(R.id.childTextView)
        childTextView.text = childText

        // Set different text and background color for different child items
        if (groupPosition == 0 && childPosition == 0) {
            // Set colors for the first child item of the first group
            childTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        } else {
            // Set colors for other child items
            childTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}



@RequiresApi(Build.VERSION_CODES.Q)
fun generateExcelSheetStatement(context: Context, userdetails: MutableList<Map<String, Any?>>,title: String) {
    try {

        val uniqueId = UUID.randomUUID().toString()
        val fileName = "${title}_$uniqueId.xls"

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
                val sheet: WritableSheet = writableWorkbook.createSheet(title, 0)



                sheet


                // Create a writable font with bold style and increased size for the headers
                val boldFont: WritableFont = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)

                boldFont.colour = Colour.WHITE // Set font color for headers to white

                // Create a writable cell format with the bold font
                val boldCellFormat: WritableCellFormat = WritableCellFormat(boldFont)
                boldCellFormat.alignment = Alignment.CENTRE // Center align the headers
                boldCellFormat.setBackground(Colour.SKY_BLUE) // Set background color
                boldCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.WHITE)
                boldCellFormat.verticalAlignment = VerticalAlignment.CENTRE


                val dataFont: WritableFont = WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD)
                dataFont.colour = Colour.WHITE // Set font color for headers to white
                // Create a writable cell format with the bold font
                val dataCellFormat: WritableCellFormat = WritableCellFormat(dataFont)
                dataCellFormat.alignment = Alignment.CENTRE // Center align the headers
                dataCellFormat.setBackground(Colour.BLUE_GREY) // Set background color
                dataCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.WHITE)
                dataCellFormat.verticalAlignment = VerticalAlignment.CENTRE





                //----Title Cell----

                // Create header row style and cell view
                val titleFont = WritableFont(WritableFont.ARIAL, 15, WritableFont.BOLD)
                titleFont.colour = Colour.WHITE // Set font color for headers to white

                val titleCellFormat = WritableCellFormat(titleFont)

                titleCellFormat.setBackground(Colour.DARK_BLUE) // Set background color
                titleCellFormat.wrap = true

                titleCellFormat.verticalAlignment = VerticalAlignment.CENTRE
                titleCellFormat.alignment = Alignment.CENTRE
                titleCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.WHITE)
                // Merge cells for the title row
                sheet.mergeCells(0, 0, 4, 0) // Merge cells from column 0 to column 4 (all columns), row 0
                // Add the title label to the merged cell
                val titleCell = Label(0, 0, "ACK ST. STEPHENS KAYO ${title}", titleCellFormat)

                sheet.addCell(titleCell)

                // Create header row
                // Create header row
                val headerIndex: Label = Label(0, 1, "#", boldCellFormat)
                val headerName: Label = Label(1, 1, "Name",boldCellFormat)
                val headerDate: Label = Label(2, 1, "Date Joined",boldCellFormat)
                val headerAmount: Label = Label(3, 1, "Total paid",boldCellFormat)
                val headerBalance: Label = Label(4, 1, "Balance",boldCellFormat)


// Add the header labels to the sheet
                sheet.addCell(headerIndex)
                sheet.addCell(headerName)

                sheet.addCell(headerDate)
                sheet.addCell(headerAmount)
                sheet.addCell(headerBalance)



                // Create data rows
                var rowIndex = 2
                for (usr in userdetails) {
                    val indexCell: Label = Label(0, rowIndex, (rowIndex-1).toString(), dataCellFormat)
                    val nameCell: Label = Label(1, rowIndex, usr["name"].toString(), dataCellFormat)
                    val dateCell: Label = Label(2, rowIndex, usr["dateJoined"].toString(), dataCellFormat)
                    val totalCell: Label = Label(3, rowIndex, usr["totalPaid"].toString(), dataCellFormat)
                    val balanceCell: Label = Label(4, rowIndex, usr["balance"].toString(), dataCellFormat) // Add the "Name" data to column 1

                    sheet.addCell(indexCell)
                    sheet.addCell(nameCell)
                    sheet.addCell(dateCell)
                    sheet.addCell(totalCell)
                    sheet.addCell(balanceCell)



                    rowIndex++
                }


                // Auto-size the columns to fit the content
                for (col in 0 until sheet.columns) {
                    //sheet.autoSizeColumn(col)

                }
                // Manually calculate and set column widths based on content
                for (col in 1 until sheet.columns) {
                    val longestCellContent = sheet.getColumn(col).map { it.contents.length }.maxOrNull() ?: 10

                    sheet.setColumnView(col, longestCellContent + 2) // Adding some padding for readability
                }

                // Calculate row heights based on content size and add padding
                val padding = 20 // Adjust this value to set the padding
                for (rowIndex in 0 until userdetails.size + 1) {
                    sheet.setRowView(rowIndex, rowHeightWithPadding(sheet.getRowView(rowIndex), padding))
                }

                // Write and close the workbook
                writableWorkbook.write()
                writableWorkbook.close()

                // Close the output stream
                outputStream.close()
                showSnackbarWithOpenButton(context, uri,"Excel file saved successfully!")

                // Toast.makeText(context, "Excel file saved successfully!", Toast.LENGTH_SHORT).show()
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
// Function to set the border style for a cell format
private fun setBorderStyle(cellFormat: WritableCellFormat, border: Border, lineStyle: BorderLineStyle, color: Colour) {
    cellFormat.setBorder(border, lineStyle)
    cellFormat.setBorder(border,lineStyle,color)
}

// Helper function to calculate row height with padding
fun rowHeightWithPadding(row: CellView, padding: Int): Int {
    val heightWithoutPadding = row.size
    return heightWithoutPadding + padding
}

@RequiresApi(Build.VERSION_CODES.Q)
fun generatePdfStatement(context: Context, userdetails: MutableList<Map<String, Any?>>, title: String) {
    val uniqueId = UUID.randomUUID().toString()
    val fileName = "${title}_$uniqueId.pdf"

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
                val titleParagraph = Paragraph("ACK ST. STEPHENS KAYO ${title}", titleFont)
                titleParagraph.alignment = Paragraph.ALIGN_CENTER
                titleParagraph.spacingAfter = 10f
                document.add(titleParagraph)

                // Create the table with 3 columns
                val table = PdfPTable(5)

                // Set table properties
                table.widthPercentage = 100f
                table.setHeaderRows(1) // The first row will be treated as the header

                // Set column widths (adjust as needed)
                table.setWidths(floatArrayOf(1f, 3f, 3f,3f,3f))

                // Set table border color
                table.defaultCell.borderColor = BaseColor.WHITE


                val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f, BaseColor.WHITE)
                val dataFont = FontFactory.getFont(FontFactory.HELVETICA, 12f, BaseColor.WHITE)
                // Define the heading row background color
                val headingBackgroundColor = BaseColor(102, 0, 0)

                // Add table headers with custom style
                addCellWithCustomStyle(table, "#", headingBackgroundColor,headerFont)
                addCellWithCustomStyle(table, "Name", headingBackgroundColor,headerFont)
                addCellWithCustomStyle(table, "Date Joined", headingBackgroundColor,headerFont)
                addCellWithCustomStyle(table, "Total Paid", headingBackgroundColor,headerFont)
                addCellWithCustomStyle(table, "Balance", headingBackgroundColor,headerFont)

                // Define the data row background color
                val dataBackgroundColor = BaseColor(255, 102, 102)

                // Add data rows with custom style
                var rowIndex = 1
                for (user in userdetails) {
                    addCellWithCustomStyle(table, rowIndex.toString(), dataBackgroundColor,dataFont)
                    addCellWithCustomStyle(table, user["name"].toString(), dataBackgroundColor,dataFont)
                    addCellWithCustomStyle(table, user["dateJoined"].toString(), dataBackgroundColor,dataFont)
                    addCellWithCustomStyle(table, user["totalPaid"].toString(), dataBackgroundColor,dataFont)
                    addCellWithCustomStyle(table, user["balance"].toString(), dataBackgroundColor,dataFont)
                    rowIndex++
                }

                // Add the table to the document
                document.add(table)

                // Close the document
                document.close()

                // Close the output stream
                outputStream.close()

                // Show the Snackbar with the 'Open' button
                showSnackbarWithOpenButton(context, uri, "PDF file saved successfully!")
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
private fun addCellWithCustomStyle(table: PdfPTable, text: String, backgroundColor: BaseColor, font: Font) {
    val cell = PdfPCell(Paragraph(text, font))
    cell.backgroundColor = backgroundColor
    cell.borderColor = BaseColor.WHITE
    cell.setPadding(5f)
    table.addCell(cell)
}

// Function to show Snackbar with 'Open' button
private fun showSnackbarWithOpenButton(context: Context, fileUri: Uri, message:String) {
    val parentView = (context as Activity).findViewById<View>(android.R.id.content)

    val snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_INDEFINITE)
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
