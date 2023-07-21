package com.ack.ststephenskayo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LeaderAdapter(var leadersList: List<LeaderModel>) : RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_leader, parent, false)
        return LeaderViewHolder(view)
    }
    override fun onBindViewHolder(holder: LeaderViewHolder, position: Int) {
        val leader = leadersList[position]
        holder.bind(leader)
    }


    override fun getItemCount(): Int = leadersList.size

    inner class LeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define the UI elements for the CardView here
        private val leaderNameTextView: TextView = itemView.findViewById(R.id.leader_name)
        private val leaderPositionTextView: TextView = itemView.findViewById(R.id.leader_position)

        fun bind(leader: LeaderModel) {
            // Bind the data to the UI elements in the CardView
            leaderNameTextView.text = leader.name
            leaderPositionTextView.text = leader.position
        }
    }
}
