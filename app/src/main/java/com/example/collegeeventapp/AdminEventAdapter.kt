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

class AdminEventAdapter(
    private val eventList: ArrayList<Event>,
    private val listener: OnEventActionListener
) : RecyclerView.Adapter<AdminEventAdapter.EventViewHolder>() {

    interface OnEventActionListener {
        fun onEdit(event: Event)
        fun onDelete(event: Event)
        fun onRegistrations(event: Event)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val venue: TextView = itemView.findViewById(R.id.tvVenue)
        val eventImage: ImageView = itemView.findViewById(R.id.ivEventImage)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnRegistrations: Button =
            itemView.findViewById(R.id.btnRegistrations)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_event, parent, false)

        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        val currentEvent = eventList[position]

        holder.title.text = currentEvent.title
        holder.description.text = currentEvent.description
        holder.venue.text = currentEvent.venue

        // Format Date for Chip
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

        holder.btnEdit.setOnClickListener {
            listener.onEdit(currentEvent)
        }

        holder.btnDelete.setOnClickListener {
            listener.onDelete(currentEvent)
        }

        holder.btnRegistrations.setOnClickListener {
            listener.onRegistrations(currentEvent)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}