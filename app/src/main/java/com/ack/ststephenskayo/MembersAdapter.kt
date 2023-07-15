package com.ack.ststephenskayo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ack.ststephenskayo.Member
import com.ack.ststephenskayo.R

class MembersAdapter(private val membersList: List<Member>) :
    RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.members_table_item, parent, false)
        return MemberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val currentMember = membersList[position]
        holder.nameTextView.text = currentMember.name
        holder.dateJoinedTextView.text = currentMember.dateJoined
        holder.totalPaidTextView.text = currentMember.totalPaid.toString()
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.member_name)
        val dateJoinedTextView: TextView = itemView.findViewById(R.id.member_date_joined)
        val totalPaidTextView: TextView = itemView.findViewById(R.id.member_total_paid)
    }
}
