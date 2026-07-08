package com.example.collegeeventapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminEventAdapter(
    private val eventList: ArrayList<Event>,
    private val listener: OnEventActionListener
) : RecyclerView.Adapter<AdminEventAdapter.EventViewHolder>() {

    interface OnEventActionListener {
        fun onEdit(event: Event)
        fun onDelete(event: Event)
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val venue: TextView = itemView.findViewById(R.id.tvVenue)
        val ibMenue: ImageButton = itemView.findViewById(R.id.ibMenu)

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
        holder.date.text = currentEvent.date
        holder.venue.text = currentEvent.venue

        holder.ibMenue.setOnClickListener {

            val popupMenu = PopupMenu(holder.itemView.context, holder.ibMenue)

            popupMenu.menuInflater.inflate(
                R.menu.admin_event_menu,
                popupMenu.menu
            )

            popupMenu.setOnMenuItemClickListener {

                when (it.itemId) {

                    R.id.menuEdit -> {
                        listener.onEdit(currentEvent)
                        true
                    }

                    R.id.menuDelete -> {
                        listener.onDelete(currentEvent)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()

        }

    }

    override fun getItemCount(): Int {

        return eventList.size

    }

}