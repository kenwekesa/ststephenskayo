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





package com.ack.ststephenskayo

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Liturgy : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: MyExpandableListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest)

        expandableListView = findViewById(R.id.expandableListView)
        adapter = MyExpandableListAdapter()
        expandableListView.setAdapter(adapter)

        // Fetch data from Firestore and update the adapter
        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()

        // Assuming your Firestore collection is "liturgyData" and document ID is "your_document_id"
        val liturgyRef = firestore.collection("liturgyData").document("data")

        liturgyRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val articles = documentSnapshot.get("articles") as? List<Map<String, Any>>

                // Convert the list of maps to a list of Item objects
                val itemList = articles?.map { map ->
                    Item(
                        map["article"] as? Int ?: 0,
                        map["title"] as? String ?: "",
                        map["content"] as? String ?: ""
                    )
                } ?: emptyList()

                // Update the adapter with the list of Item objects
                adapter.updateDataList(itemList)
            }
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
            dataList = items.toMutableList()
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
            groupTextView.text = getGroup(groupPosition).toString()
            return view
        }

        override fun getChildView(
            groupPosition: Int,
            childPosition: Int,
            isLastChild: Boolean,
            convertView: View?,
            parent: ViewGroup?
        ): View {
            val view = layoutInflater.inflate(R.layout.child_item_layout, parent, false)
            val childTextView = view.findViewById<TextView>(R.id.childTextView)
            childTextView.text = getChild(groupPosition, childPosition).toString()
            return view
        }
    }
}
