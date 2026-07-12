package com.example.collegeeventapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale


class EventAdapter(
    private val eventList: MutableList<Event>,
private val listener: OnRegisterClickListener,
private val showRegisterButton: Boolean = true
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    interface OnRegisterClickListener {
        fun onRegister(event: Event)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val venue: TextView = itemView.findViewById(R.id.tvVenue)
        val eventImage: ImageView = itemView.findViewById(R.id.ivEventImage)
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
        holder.venue.text = currentEvent.venue

        // Format Date for Chip (e.g., 26-08-2026 -> 26 Aug)
        try {
            val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            val date = inputFormat.parse(currentEvent.date)
            holder.date.text = outputFormat.format(date!!)
        } catch (e: Exception) {
            holder.date.text = currentEvent.date
        }

        Glide.with(holder.itemView.context)
            .load(currentEvent.imageUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_placeholder)
            .into(holder.eventImage)

        if (!showRegisterButton) {

            holder.btnRegister.visibility = View.GONE

        } else {

            holder.btnRegister.visibility = View.VISIBLE

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

    }

    override fun getItemCount(): Int {

        return eventList.size

    }


}