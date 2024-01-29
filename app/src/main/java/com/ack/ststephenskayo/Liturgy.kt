//package com.ack.ststephenskayo
//import android.os.Bundle
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseExpandableListAdapter
//import android.widget.ExpandableListView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.ack.ststephenskayo.R
//import com.google.firebase.firestore.FirebaseFirestore
//
//class Liturgy : AppCompatActivity() {
//
//    private lateinit var expandableListView: ExpandableListView
//    private lateinit var adapter: MyExpandableListAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_guest)
//
//        expandableListView = findViewById(R.id.expandableListView)
//        adapter = MyExpandableListAdapter()
//        expandableListView.setAdapter(adapter)
//
//        // Fetch data from Firestore and update the adapter
//        fetchDataFromFirestore()
//    }
//
//
//
//
//
//    private fun fetchDataFromFirestore() {
//        val firestore = FirebaseFirestore.getInstance()
//
//        // Assuming your Firestore collection is "liturgyData" and document ID is "your_document_id"
//        val liturgyRef = firestore.collection("liturgyData").document("your_document_id")
//
//        liturgyRef.get().addOnSuccessListener { documentSnapshot ->
//            if (documentSnapshot.exists()) {
//                val articles = documentSnapshot.get("articles") as? List<Map<String, Any>>
//
//                // Convert the list of maps to a list of Item objects
//                val itemList = articles?.map { map ->
//                    Item(
//                        map["article"] as? Int ?: 0,
//                        map["title"] as? String ?: "",
//                        map["content"] as? String ?: ""
//                    )
//                } ?: emptyList()
//
//                // Update the adapter with the list of Item objects
//                adapter.updateDataList(itemList)
//            }
//        }
//    }
//    data class ItemsData(
//        val items: List<Item>
//    )
//
//
//
//
//    data class Item(
//        val article: Int,
//        val title: String,
//        val content: String
//    )
//
//    private inner class MyExpandableListAdapter : BaseExpandableListAdapter() {
//        private var dataList: List<Item> = emptyList()
//
////        fun updateDataList(items: List<Item>) {
////            dataList = items
////            notifyDataSetChanged()
////        }
//        fun updateDataList(items: List<Item>) {
//            dataList = items.toMutableList()
//            notifyDataSetChanged()
//        }
//
//        override fun getGroupCount(): Int {
//            return dataList.size
//        }
//
//        override fun getChildrenCount(groupPosition: Int): Int {
//            // Each group has only one child
//            return 1
//        }
//
//        override fun getGroup(groupPosition: Int): Any {
//            val item = dataList[groupPosition]
//            return "${item.title} ${item.article}"
//        }
//
//        override fun getChild(groupPosition: Int, childPosition: Int): Any {
//            return dataList[groupPosition].content
//        }
//
//
//        override fun getGroupId(groupPosition: Int): Long {
//            return groupPosition.toLong()
//        }
//
//        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
//            return childPosition.toLong()
//        }
//
//        override fun hasStableIds(): Boolean {
//            return false
//        }
//
//        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
//            return true
//        }
//
//        override fun getGroupView(
//            groupPosition: Int,
//            isExpanded: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
//            val groupTextView = view.findViewById<TextView>(R.id.groupTextView)
//            groupTextView.text = getGroup(groupPosition).toString()
//            return view
//        }
//
//        override fun getChildView(
//            groupPosition: Int,
//            childPosition: Int,
//            isLastChild: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
//            val childTextView = view.findViewById<TextView>(R.id.childTextView)
//            childTextView.text = getChild(groupPosition, childPosition).toString()
//            return view
//        }
//    }
//}




//
//package com.ack.ststephenskayo
//
//import android.os.Bundle
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseExpandableListAdapter
//import android.widget.ExpandableListView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.firestore.FirebaseFirestore
//
//class Liturgy : AppCompatActivity() {
//
//    private lateinit var expandableListView: ExpandableListView
//    private lateinit var adapter: MyExpandableListAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_guest)
//
//        expandableListView = findViewById(R.id.expandableListView)
//        adapter = MyExpandableListAdapter()
//        expandableListView.setAdapter(adapter)
//
//        // Fetch all documents from Firestore collection and update the adapter
//        fetchAllDocumentsFromFirestore()
//    }
//
//    private fun fetchAllDocumentsFromFirestore() {
//        val firestore = FirebaseFirestore.getInstance()
//
//        // Assuming your Firestore collection is "liturgyData"
//        val liturgyCollectionRef = firestore.collection("liturgyData")
//
//        liturgyCollectionRef.get().addOnSuccessListener { querySnapshot ->
//            val itemList = mutableListOf<Item>()
//
//            for (documentSnapshot in querySnapshot.documents) {
//                val article = documentSnapshot.getLong("artitle")?.toInt() ?: 0
//                val title = documentSnapshot.getString("title") ?: ""
//                val content = documentSnapshot.getString("content") ?: ""
//
//                itemList.add(Item(article, title, content))
//            }
//
//            runOnUiThread {
//                adapter.updateDataList(itemList)
//            }
//        }.addOnFailureListener { e ->
//            // Handle failure, show an error message, log the exception, etc.
//        }
//    }
//
//    data class Item(
//        val article: Int,
//        val title: String,
//        val content: String
//    )
//
//    private inner class MyExpandableListAdapter : BaseExpandableListAdapter() {
//        private var dataList: List<Item> = emptyList()
//
//        fun updateDataList(items: List<Item>) {
//            dataList = items
//            notifyDataSetChanged()
//        }
//
//        override fun getGroupCount(): Int {
//            return dataList.size
//        }
//
//        override fun getChildrenCount(groupPosition: Int): Int {
//            // Each group has only one child
//            return 1
//        }
//
//        override fun getGroup(groupPosition: Int): Any {
//            val item = dataList[groupPosition]
//            return "${item.title} ${item.article}"
//        }
//
//        override fun getChild(groupPosition: Int, childPosition: Int): Any {
//            return dataList[groupPosition].content
//        }
//
//        override fun getGroupId(groupPosition: Int): Long {
//            return groupPosition.toLong()
//        }
//
//        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
//            return childPosition.toLong()
//        }
//
//        override fun hasStableIds(): Boolean {
//            return false
//        }
//
//        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
//            return true
//        }
//
//        override fun getGroupView(
//            groupPosition: Int,
//            isExpanded: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
//            val groupTextView = view.findViewById<TextView>(R.id.groupTextView)
//            groupTextView.text = getGroup(groupPosition).toString()
//            return view
//        }
//
//        override fun getChildView(
//            groupPosition: Int,
//            childPosition: Int,
//            isLastChild: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
//            val childTextView = view.findViewById<TextView>(R.id.childTextView)
//            childTextView.text = getChild(groupPosition, childPosition).toString()
//            return view
//        }
//    }
//}
//

//package com.ack.ststephenskayo
//
//import android.os.Bundle
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseExpandableListAdapter
//import android.widget.ExpandableListView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.firestore.FirebaseFirestore
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.style.StyleSpan
//
//class Liturgy : AppCompatActivity() {
//
//    private lateinit var expandableListView: ExpandableListView
//    private lateinit var adapter: MyExpandableListAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_guest)
//
//        expandableListView = findViewById(R.id.expandableListView)
//        adapter = MyExpandableListAdapter()
//        expandableListView.setAdapter(adapter)
//
//        // Fetch all documents from Firestore collection and update the adapter
//        fetchAllDocumentsFromFirestore()
//    }
//
//    private fun fetchAllDocumentsFromFirestore() {
//        val firestore = FirebaseFirestore.getInstance()
//
//        // Assuming your Firestore collection is "liturgyData"
//        val liturgyCollectionRef = firestore.collection("liturgyData")
//
//        liturgyCollectionRef.get().addOnSuccessListener { querySnapshot ->
//            val itemList = mutableListOf<Item>()
//
//            for (documentSnapshot in querySnapshot.documents) {
//                val article = documentSnapshot.getLong("artitle")?.toInt() ?: 0
//                val title = documentSnapshot.getString("title") ?: ""
//                val content = documentSnapshot.getString("content") ?: ""
//
//                itemList.add(Item(article, title, content))
//            }
//
//            runOnUiThread {
//                adapter.updateDataList(itemList)
//            }
//        }.addOnFailureListener { e ->
//            // Handle failure, show an error message, log the exception, etc.
//        }
//    }
//
//    data class Item(
//        val article: Int,
//        val title: String,
//        val content: String
//    )
//
//    private inner class MyExpandableListAdapter : BaseExpandableListAdapter() {
//        private var dataList: List<Item> = emptyList()
//
//        fun updateDataList(items: List<Item>) {
//            dataList = items
//            notifyDataSetChanged()
//        }
//
//        override fun getGroupCount(): Int {
//            return dataList.size
//        }
//
//        override fun getChildrenCount(groupPosition: Int): Int {
//            // Each group has only one child
//            return 1
//        }
//
//        override fun getGroup(groupPosition: Int): Any {
//            val item = dataList[groupPosition]
//            return "${item.title} ${item.article}"
//        }
//
//        override fun getChild(groupPosition: Int, childPosition: Int): Any {
//            return dataList[groupPosition].content
//        }
//
//        override fun getGroupId(groupPosition: Int): Long {
//            return groupPosition.toLong()
//        }
//
//        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
//            return childPosition.toLong()
//        }
//
//        override fun hasStableIds(): Boolean {
//            return false
//        }
//
//        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
//            return true
//        }
//
//        override fun getGroupView(
//            groupPosition: Int,
//            isExpanded: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
//            val groupTextView = view.findViewById<TextView>(R.id.groupTextView)
//            val groupText = getGroup(groupPosition).toString()
//
//            // Apply bold formatting to the specific part of the text
//            val startIndex = groupText.indexOf("**")
//            val endIndex = groupText.lastIndexOf("**") + 2 // Adding 2 to include the closing "**"
//
//            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
//                val spannableString = SpannableString(groupText)
//                spannableString.setSpan(
//                    StyleSpan(android.graphics.Typeface.BOLD),
//                    startIndex,
//                    endIndex,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//                groupTextView.text = spannableString
//            } else {
//                groupTextView.text = groupText
//            }
//
//            return view
//        }
//
//        override fun getChildView(
//            groupPosition: Int,
//            childPosition: Int,
//            isLastChild: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
//            val childTextView = view.findViewById<TextView>(R.id.childTextView)
//            childTextView.text = getChild(groupPosition, childPosition).toString()
//            return view
//        }
//    }
//}





//
//
//
//
//
//package com.ack.ststephenskayo
//
//import android.os.Bundle
//import android.text.Spannable
//import android.text.SpannableString
//import android.text.style.StyleSpan
//import android.view.View
//import android.view.ViewGroup
//import android.widget.BaseExpandableListAdapter
//import android.widget.ExpandableListView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.firestore.FirebaseFirestore
//
//class Liturgy : AppCompatActivity() {
//
//    private lateinit var expandableListView: ExpandableListView
//    private lateinit var adapter: MyExpandableListAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_guest)
//
//        expandableListView = findViewById(R.id.expandableListView)
//        adapter = MyExpandableListAdapter()
//        expandableListView.setAdapter(adapter)
//
//        // Fetch all documents from Firestore collection and update the adapter
//        fetchAllDocumentsFromFirestore()
//    }
//
//    private fun fetchAllDocumentsFromFirestore() {
//        val firestore = FirebaseFirestore.getInstance()
//
//        // Assuming your Firestore collection is "liturgyData"
//        val liturgyCollectionRef = firestore.collection("liturgyData")
//
//        liturgyCollectionRef.get().addOnSuccessListener { querySnapshot ->
//            val itemList = mutableListOf<Item>()
//
//            for (documentSnapshot in querySnapshot.documents) {
//                val article = documentSnapshot.getLong("artitle")?.toInt() ?: 0
//                val title = documentSnapshot.getString("title") ?: ""
//                val content = documentSnapshot.getString("content") ?: ""
//
//                itemList.add(Item(article, title, content))
//            }
//
//            runOnUiThread {
//                adapter.updateDataList(itemList)
//            }
//        }.addOnFailureListener { e ->
//            // Handle failure, show an error message, log the exception, etc.
//        }
//    }
//
//    data class Item(
//        val article: Int,
//        val title: String,
//        val content: String
//    )
//
//    private inner class MyExpandableListAdapter : BaseExpandableListAdapter() {
//        private var dataList: List<Item> = emptyList()
//
//        fun updateDataList(items: List<Item>) {
//            dataList = items
//            notifyDataSetChanged()
//        }
//
//        override fun getGroupCount(): Int {
//            return dataList.size
//        }
//
//        override fun getChildrenCount(groupPosition: Int): Int {
//            // Each group has only one child
//            return 1
//        }
//
//        override fun getGroup(groupPosition: Int): Any {
//            val item = dataList[groupPosition]
//            return "${item.title} ${item.article}"
//        }
//
//        override fun getChild(groupPosition: Int, childPosition: Int): Any {
//            return dataList[groupPosition].content
//        }
//
//        override fun getGroupId(groupPosition: Int): Long {
//            return groupPosition.toLong()
//        }
//
//        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
//            return childPosition.toLong()
//        }
//
//        override fun hasStableIds(): Boolean {
//            return false
//        }
//
//        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
//            return true
//        }
//
//        override fun getGroupView(
//            groupPosition: Int,
//            isExpanded: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
//            val groupTextView = view.findViewById<TextView>(R.id.groupTextView)
//            val groupText = getGroup(groupPosition).toString()
//
//            val boldMarker = "**"
//
//            if (groupText.contains(boldMarker)) {
//                val startIndex = groupText.indexOf(boldMarker)
//                val endIndex = groupText.lastIndexOf(boldMarker) + boldMarker.length
//
//                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
//                    val boldText = groupText.substring(startIndex + boldMarker.length, endIndex - boldMarker.length)
//                    val spannableString = SpannableString(boldText)
//                    spannableString.setSpan(
//                        StyleSpan(android.graphics.Typeface.BOLD),
//                        0,
//                        boldText.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    groupTextView.text = spannableString
//                }
//            } else {
//                groupTextView.text = groupText
//            }
//
//            return view
//        }
//
//        override fun getChildView(
//            groupPosition: Int,
//            childPosition: Int,
//            isLastChild: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
//            val childTextView = view.findViewById<TextView>(R.id.childTextView)
//            childTextView.text = getChild(groupPosition, childPosition).toString()
//            return view
//        }
//    }
//}


//            if (childText.contains(boldMarker)) {
//                val startIndex = childText.indexOf(boldMarker)
//                val endIndex = childText.lastIndexOf(boldMarker) + boldMarker.length
//
//                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
//                    val formattedText = childText.substring(startIndex + boldMarker.length, endIndex - boldMarker.length)
//
//                    // Apply bold and dark blue color to formattedText
//                    val spannableString = SpannableString(childText)
//                    spannableString.setSpan(
//                        StyleSpan(android.graphics.Typeface.BOLD),
//                        startIndex + boldMarker.length,  // Adjust start index for removed markers
//                        endIndex - boldMarker.length,   // Adjust end index for removed markers
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    spannableString.setSpan(
//                        ForegroundColorSpan(ContextCompat.getColor(this@Liturgy, R.color.dark_blue)),
//                        startIndex + boldMarker.length,
//                        endIndex - boldMarker.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//
//                    // Apply bold style to the rest of the text
//                    spannableString.setSpan(
//                        StyleSpan(android.graphics.Typeface.BOLD),
//                        0,
//                        startIndex,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//                    spannableString.setSpan(
//                        StyleSpan(android.graphics.Typeface.BOLD),
//                        endIndex,
//                        childText.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//
//                    childTextView.text = spannableString
//                }

package com.ack.ststephenskayo

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

class Liturgy : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: MyExpandableListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)

        expandableListView = findViewById(R.id.expandableListView)
        adapter = MyExpandableListAdapter()
        expandableListView.setAdapter(adapter)

        // Fetch all documents from Firestore collection and update the adapter
        fetchAllDocumentsFromFirestore()
    }

    private fun fetchAllDocumentsFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()

        // Assuming your Firestore collection is "liturgyData"
        val liturgyCollectionRef = firestore.collection("liturgyData")

        liturgyCollectionRef.get().addOnSuccessListener { querySnapshot ->
            val itemList = mutableListOf<Item>()

            for (documentSnapshot in querySnapshot.documents) {
                val article = documentSnapshot.getLong("artitle")?.toInt() ?: 0
                val title = documentSnapshot.getString("title") ?: ""
                val content = documentSnapshot.getString("content") ?.replace("\n", System.lineSeparator()) ?: ""

                itemList.add(Item(article, title, content))
            }

            runOnUiThread {
                adapter.updateDataList(itemList)
            }
        }.addOnFailureListener { e ->
            // Handle failure, show an error message, log the exception, etc.
        }
    }

    data class Item(
        val article: Int,
        val title: String,
        val content: String
    )

    private inner class MyExpandableListAdapter : BaseExpandableListAdapter() {
        private var dataList: List<Item> = emptyList()

        fun updateDataList(items: List<Item>) {
            dataList = items
            notifyDataSetChanged()
        }

        override fun getGroupCount(): Int {
            return dataList.size
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            // Each group has only one child
            return 1
        }

        override fun getGroup(groupPosition: Int): Any {
            val item = dataList[groupPosition]
            return "${item.title} ${item.article}"
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return dataList[groupPosition].content
        }

        override fun getGroupId(groupPosition: Int): Long {
            return groupPosition.toLong()
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            return childPosition.toLong()
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }

        override fun getGroupView(
            groupPosition: Int,
            isExpanded: Boolean,
            convertView: View?,
            parent: ViewGroup?
        ): View {
            val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
            val groupTextView = view.findViewById<TextView>(R.id.groupTextView)
            val groupText = getGroup(groupPosition).toString()

            val boldMarker = "**"

            if (groupText.contains(boldMarker)) {
                val startIndex = groupText.indexOf(boldMarker)
                val endIndex = groupText.lastIndexOf(boldMarker) + boldMarker.length

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    val formattedText = groupText.substring(startIndex + boldMarker.length, endIndex - boldMarker.length)

                    // Apply bold style to the formattedText
                    val spannableString = SpannableString(groupText.replace("$boldMarker$formattedText$boldMarker", ""))
                    spannableString.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        0,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    groupTextView.text = spannableString
                    groupTextView.setTextColor(ContextCompat.getColor(this@Liturgy, R.color.dark_blue))
                }
            } else {
                // If there is no boldMarker, apply bold style to the entire text
                val spannableString = SpannableString(groupText)
                spannableString.setSpan(
                    StyleSpan(android.graphics.Typeface.BOLD),
                    0,
                    groupText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                groupTextView.text = spannableString
            }

            return view
        }

//        override fun getChildView(
//            groupPosition: Int,
//            childPosition: Int,
//            isLastChild: Boolean,
//            convertView: View?,
//            parent: ViewGroup?
//        ): View {
//            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
//            val childTextView = view.findViewById<TextView>(R.id.childTextView)
//            val childText = getChild(groupPosition, childPosition).toString()
//
//            val boldMarker = "**"
//
//            if (childText.contains(boldMarker)) {
//                val startIndex = childText.indexOf(boldMarker)
//                val endIndex = childText.lastIndexOf(boldMarker) + boldMarker.length
//
//                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
//                    val formattedText = childText.substring(startIndex + boldMarker.length, endIndex - boldMarker.length)
//
//                    // Apply bold style to the entire text
//                    val spannableString = SpannableString(childText)
//                    spannableString.setSpan(
//                        StyleSpan(android.graphics.Typeface.BOLD),
//                        0,
//                        childText.length,
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//
//                    // Apply dark blue color only to the formattedText
//                    spannableString.setSpan(
//                        ForegroundColorSpan(ContextCompat.getColor(this@Liturgy, R.color.dark_blue)),
//                        startIndex,
//                        endIndex - boldMarker.length * 2,  // Subtracting twice boldMarker.length to account for both markers
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
//
//                    childTextView.text = spannableString
//                }
//            } else {
//                // If there is no boldMarker, apply bold style to the entire text
//                val spannableString = SpannableString(childText)
//                spannableString.setSpan(
//                    StyleSpan(android.graphics.Typeface.BOLD),
//                    0,
//                    childText.length,
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                )
//
//                childTextView.text = spannableString
//            }
//
//            return view
//        }

        override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View?,
            parent: ViewGroup?
        ): View {
            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
            val childTextView = view.findViewById<TextView>(R.id.childTextView)
            val childText = getChild(groupPosition, childPosition).toString()

            val boldMarker = "**"

            if (childText.contains(boldMarker)) {
                val startIndex = childText.indexOf(boldMarker)
                val endIndex = childText.lastIndexOf(boldMarker) + boldMarker.length

                if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                    val formattedText = childText.substring(startIndex + boldMarker.length, endIndex - boldMarker.length)

                    // Create a new spannableString without the markers
                    val spannableString = SpannableString(formattedText)
                    spannableString.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        0,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Apply dark blue color to the formattedText
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this@Liturgy, R.color.dark_blue)),
                        0,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    // Combine the formattedText with the rest of the childText
                    val resultString = SpannableString(childText.replace("$boldMarker$formattedText$boldMarker", formattedText))
                    resultString.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        0,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    resultString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this@Liturgy, R.color.dark_blue)),
                        0,
                        formattedText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    childTextView.text = resultString
                }
            } else {
                // If there is no boldMarker, apply bold style to the entire text
                val spannableString = SpannableString(childText)
                spannableString.setSpan(
                    StyleSpan(android.graphics.Typeface.BOLD),
                    0,
                    childText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this@Liturgy, R.color.black)),
                    0,
                    childText.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                childTextView.text = spannableString
            }

            return view
        }

    }
}
