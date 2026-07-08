package com.example.collegeeventapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardActivity : AppCompatActivity(),
    EventAdapter.OnRegisterClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var tvNoEvents: TextView

    private val eventList = ArrayList<Event>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        tvNoEvents = findViewById(R.id.tvNoEvents)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = EventAdapter(eventList, this)
        recyclerView.adapter = adapter

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
                    this,
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
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show()
            return
        }

        val registrationId = "${student.uid}_${event.id}"

        db.collection("registrations")
            .document(registrationId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    Toast.makeText(
                        this,
                        "You have already registered for this event",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    val registration = Registration(
                        eventId = event.id,
                        eventTitle = event.title,
                        eventDate = event.date,
                        eventVenue = event.venue,
                        studentUid = student.uid,
                        studentEmail = student.email ?: ""
                    )

                    db.collection("registrations")
                        .document(registrationId)
                        .set(registration)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Registration Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            event.isRegistered = true
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { e ->

                            Toast.makeText(
                                this,
                                e.message ?: "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                }

            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    this,
                    e.message ?: "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()

            }

    }

}