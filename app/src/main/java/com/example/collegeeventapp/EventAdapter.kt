package com.example.collegeeventapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val eventList: ArrayList<Event>,
    private val listener: OnRegisterClickListener
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    interface OnRegisterClickListener {
        fun onRegister(event: Event)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val venue: TextView = itemView.findViewById(R.id.tvVenue)
        val btnRegister: Button = itemView.findViewById(R.id.btnRegister)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        val currentEvent = eventList[position]

        holder.title.text = currentEvent.title
        holder.description.text = currentEvent.description
        holder.date.text = currentEvent.date
        holder.venue.text = currentEvent.venue

        if (currentEvent.isRegistered) {

            holder.btnRegister.text = "Registered"
            holder.btnRegister.isEnabled = false

        } else {

            holder.btnRegister.text = "Register"
            holder.btnRegister.isEnabled = true

            holder.btnRegister.setOnClickListener {
                listener.onRegister(currentEvent)
            }

        }

    }

    override fun getItemCount(): Int {

        return eventList.size

    }
}