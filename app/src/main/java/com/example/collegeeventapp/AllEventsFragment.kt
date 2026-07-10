package com.example.collegeeventapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AllEventsFragment : Fragment(),
    EventAdapter.OnRegisterClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var tvNoEvents: TextView

    private val eventList = ArrayList<Event>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_all_events,
            container,
            false
        )

        tvNoEvents = view.findViewById(R.id.tvNoEvents)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = EventAdapter(eventList, this)
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    private fun loadEvents() {
        eventList.clear()

        val student = auth.currentUser

        db.collection("events")
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {
                    adapter.notifyDataSetChanged()
                    updateUI()
                    return@addOnSuccessListener
                }

                var processedEvents = 0

                for (document in documents) {

                    val event = document.toObject(Event::class.java)
                    event.id = document.id

                    if (student != null) {

                        val registrationId = "${student.uid}_${event.id}"

                        db.collection("registrations")
                            .document(registrationId)
                            .get()
                            .addOnSuccessListener { registrationDocument ->

                                event.isRegistered = registrationDocument.exists()

                                eventList.add(event)

                                processedEvents++

                                if (processedEvents == documents.size()) {
                                    adapter.notifyDataSetChanged()
                                    updateUI()
                                }

                            }

                    } else {

                        eventList.add(event)

                        processedEvents++

                        if (processedEvents == documents.size()) {
                            adapter.notifyDataSetChanged()
                            updateUI()
                        }

                    }

                }

            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    requireContext(),
                    e.message ?: "Unknown error occurred",
                    Toast.LENGTH_SHORT
                ).show()

                updateUI()

            }

    }

    private fun updateUI() {
        if (eventList.isEmpty()) {

            tvNoEvents.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        } else {

            tvNoEvents.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }
    }

    override fun onRegister(event: Event) {
        val student = auth.currentUser

        if (student == null) {
            Toast.makeText( requireContext(), "Please login again", Toast.LENGTH_SHORT).show()
            return
        }

        val registrationId = "${student.uid}_${event.id}"

        db.collection("registrations")
            .document(registrationId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    Toast.makeText(
                        requireContext(),
                        "You have already registered for this event",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    db.collection("users")
                        .document(student.uid)
                        .get()
                        .addOnSuccessListener { userDocument ->

                            val studentName = userDocument.getString("name") ?: ""

                            val registration = Registration(
                                eventId = event.id,
                                eventTitle = event.title,
                                eventDate = event.date,
                                eventVenue = event.venue,
                                studentUid = student.uid,
                                studentName = studentName,
                                studentEmail = student.email ?: ""
                            )

                            db.collection("registrations")
                                .document(registrationId)
                                .set(registration)
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        requireContext(),
                                        "Registration Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    event.isRegistered = true
                                    adapter.notifyDataSetChanged()

                                }
                                .addOnFailureListener { e ->

                                    Toast.makeText(
                                        requireContext(),
                                        e.message ?: "Registration failed",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                        }

                }

            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    requireContext(),
                    e.message ?: "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }
}