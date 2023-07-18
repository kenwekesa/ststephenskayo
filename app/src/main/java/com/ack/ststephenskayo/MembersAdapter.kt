package com.ack.ststephenskayo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ack.ststephenskayo.Member
import com.ack.ststephenskayo.R

class MembersAdapter(private val membersList: List<Member>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_MEMBER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_item, parent, false)
                HeaderViewHolder(headerView)
            }
            VIEW_TYPE_MEMBER -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.members_table_item, parent, false)
                MemberViewHolder(itemView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                // Bind header views here
                holder.bindHeader()
            }
            is MemberViewHolder -> {
                val memberPosition = position - 1 // Subtract 1 to account for the header
                val currentMember = membersList[memberPosition]
                holder.bindMember(currentMember)
            }
        }
    }

    override fun getItemCount(): Int {
        // Add 1 to the item count to include the header
        return membersList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_MEMBER
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.header_name)
        private val dateJoinedTextView: TextView = itemView.findViewById(R.id.header_date_joined)
        private val totalPaidTextView: TextView = itemView.findViewById(R.id.header_total_paid)
        private val balanceTextView: TextView = itemView.findViewById(R.id.header_balance)

        fun bindHeader() {
            // Set header field values here
            nameTextView.text = ""
            dateJoinedTextView.text = ""
            totalPaidTextView.text = ""
            balanceTextView.text = ""
        }
    }

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.member_name)
        private val dateJoinedTextView: TextView = itemView.findViewById(R.id.member_date_joined)
        private val totalPaidTextView: TextView = itemView.findViewById(R.id.member_total_paid)
        private val balanceTextView: TextView = itemView.findViewById(R.id.member_balance)

        fun bindMember(member: Member) {
            // Bind member data to views here
            nameTextView.text = member.name
            dateJoinedTextView.text = member.dateJoined
            totalPaidTextView.text = member.totalPaid.toString()
            balanceTextView.text = member.balance.toString()
        }
    }
}
