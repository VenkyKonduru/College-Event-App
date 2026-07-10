package com.example.collegeeventapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyEventsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var tvNoRegisteredEvents: TextView

    private val eventList = ArrayList<Event>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_my_events, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMyEvents)
        tvNoRegisteredEvents = view.findViewById(R.id.tvNoRegisteredEvents)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = EventAdapter(
            eventList,
            object : EventAdapter.OnRegisterClickListener {
                override fun onRegister(event: Event) {
                    // Not used
                }
            },
            false
        )

        recyclerView.adapter = adapter

        loadRegisteredEvents()

        return view
    }

    private fun loadRegisteredEvents() {

        eventList.clear()

        val student = auth.currentUser ?: return

        db.collection("registrations")
            .whereEqualTo("studentUid", student.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {

                    val event = Event(
                        id = document.getString("eventId") ?: "",
                        title = document.getString("eventTitle") ?: "",
                        description = "",
                        date = document.getString("eventDate") ?: "",
                        venue = document.getString("eventVenue") ?: "",
                        isRegistered = true
                    )

                    eventList.add(event)
                }

                adapter.notifyDataSetChanged()

                if (eventList.isEmpty()) {
                    tvNoRegisteredEvents.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvNoRegisteredEvents.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
    }
}