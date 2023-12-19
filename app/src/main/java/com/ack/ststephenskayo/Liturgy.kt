package com.ack.ststephenskayo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ack.ststephenskayo.R
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

        // Fetch data from Firestore and update the adapter
        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()

        // Example: Assuming you have a collection called "liturgyData"
        val liturgyRef = firestore.collection("liturgyData").document("data")

        liturgyRef.get().addOnSuccessListener { documentSnapshot ->
            val items = documentSnapshot.toObject(ItemsData::class.java)
            items?.let {
                adapter.updateDataList(it.items)
            }
        }
    }

    data class ItemsData(val items: List<Item>)
    {
        constructor() : this(emptyList())
    }

    data class Item(val group: String, val child: List<String>)

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
            return dataList[groupPosition].child.size
        }

        override fun getGroup(groupPosition: Int): Any {
            return dataList[groupPosition].group
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any {
            return dataList[groupPosition].child[childPosition]
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
