package com.ack.ststephenskayo

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID


class Statements : AppCompatActivity() {

    val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statements)

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
                    retrieveUserData("P","Twenty")
                }
                else
                {
                    retrieveUserData("E","Twenty")
                }

            }
            //Toast.makeText(this, "Clicked: - $groupclicked - $clickedChild", Toast.LENGTH_SHORT).show()

            // Return true to indicate that the click event has been handled
            true
        }
    }


    fun retrieveUserData(report_type:String,report_category:String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                val users: MutableList<UserModel> = mutableListOf()
                val userDetailsList: MutableList<Map<String, Any?>> = mutableListOf()


                // Loop through all the children under "users" and parse them into User objects
                var report_title = ""
                for (childSnapshot in dataSnapshot.children) {
                    val user = childSnapshot.getValue(UserModel::class.java)
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
                                ) * 100).toDouble() - user.total_twenty_paid).toDouble()
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
                        //users.add(user)
                    }
                }

                // Now 'users' contains all the User objects retrieved from the database
                // You can use the data as needed, for example, passing it to your generateExcelSheetStatement or generatePdfStatement functions.

                if(report_type =="P")
                {
                    try {


                        generatePdfStatement(this@Statements, userDetailsList, report_title)
                    }
                    catch (e:Exception)
                    {
                        Log.d("Error adding file",e.stackTraceToString())
                    }
                }
                else if(report_type =="E")
                {
                    try {


                        generateExcelSheetStatement(this@Statements, userDetailsList, report_title)

                }
                catch (e:Exception)
                {
                    Log.d("Error adding file",e.stackTraceToString())
                }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if the retrieval is canceled
                Log.e("Database Error", "Error retrieving data: ${databaseError.message}")
            }
        })
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

                // Create header row
                // Create header row
                val headerIndex: Label = Label(0, 0, "#")
                val headerName: Label = Label(1, 0, "Name")
                val headerDate: Label = Label(0, 0, "Date Joined")
                val headerAmount: Label = Label(2, 0, "Total paid")
                val headerBalance: Label = Label(2, 0, "Balance")


// Add the header labels to the sheet
                sheet.addCell(headerIndex)
                sheet.addCell(headerName)

                sheet.addCell(headerDate)
                sheet.addCell(headerAmount)
                sheet.addCell(headerBalance)



                // Create data rows
                var rowIndex = 1
                for (usr in userdetails) {
                    val indexCell: Label = Label(0, rowIndex, rowIndex.toString())
                    val nameCell: Label = Label(0, rowIndex, usr["name"].toString())
                    val dateCell: Label = Label(0, rowIndex, usr["dateJoined"].toString())
                    val totalCell: Label = Label(0, rowIndex, usr["totalPaid"].toString())
                    val balanceCell: Label = Label(1, rowIndex, usr["balance"].toString()) // Add the "Name" data to column 1

                    sheet.addCell(indexCell)
                    sheet.addCell(nameCell)
                    sheet.addCell(dateCell)
                    sheet.addCell(totalCell)
                    sheet.addCell(balanceCell)



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
fun generatePdfStatement(context: Context, userdetails: MutableList<Map<String, Any?>>,title: String) {
    val fileName = "${title}.pdf"

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
                addCellWithCustomStyle(table, "#", headingBackgroundColor)
                addCellWithCustomStyle(table, "Name", headingBackgroundColor)

                addCellWithCustomStyle(table, "Date Joined", headingBackgroundColor)
                addCellWithCustomStyle(table, "Total Paid", headingBackgroundColor)
                addCellWithCustomStyle(table, "Balace", headingBackgroundColor)

                // Define the data row background color
                val dataBackgroundColor = BaseColor.LIGHT_GRAY

                // Add data rows with custom style
                var rowIndex =1
                for (user in userdetails) {

                    addCellWithCustomStyle(table, rowIndex.toString(), dataBackgroundColor)

                    addCellWithCustomStyle(table, user["name"].toString(), dataBackgroundColor)
                    addCellWithCustomStyle(table, user["dateJoined"].toString(), dataBackgroundColor)
                    addCellWithCustomStyle(table, user["totalPaid"].toString(), dataBackgroundColor)
                    addCellWithCustomStyle(table, user["balance"].toString(), dataBackgroundColor)


                    rowIndex++
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
