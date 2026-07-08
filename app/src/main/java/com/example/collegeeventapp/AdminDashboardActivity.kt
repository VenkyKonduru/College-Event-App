package com.example.collegeeventapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity(),
    AdminEventAdapter.OnEventActionListener {

    private lateinit var adapter: AdminEventAdapter
    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private lateinit var noEventAdded: TextView
    private val eventList = ArrayList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        val btnAddEvent = findViewById<Button>(R.id.btnAddEvent)
        recyclerView = findViewById(R.id.rvEvents)
        noEventAdded = findViewById(R.id.noEventAdded)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminEventAdapter(eventList, this)
        recyclerView.adapter = adapter

        loadEvents()

        btnAddEvent.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    private fun loadEvents() {

        db.collection("events")
            .get()
            .addOnSuccessListener { documents ->

                eventList.clear()

                for (document in documents) {

                    val event = document.toObject(Event::class.java)
                    event.id = document.id
                    eventList.add(event)

                }

                adapter.notifyDataSetChanged()
                updateUI()

            }
            .addOnFailureListener {

                Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show()
                updateUI()

            }

    }

    private fun updateUI() {

        if (eventList.isEmpty()) {

            noEventAdded.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        } else {

            noEventAdded.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }

    }

    override fun onEdit(event: Event) {

        val intent = Intent(this, AddEventActivity::class.java)

        intent.putExtra("isEdit", true)
        intent.putExtra("eventId", event.id)
        intent.putExtra("title", event.title)
        intent.putExtra("description", event.description)
        intent.putExtra("date", event.date)
        intent.putExtra("venue", event.venue)

        startActivity(intent)

    }

    override fun onDelete(event: Event) {



        AlertDialog.Builder(this)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { _, _ ->

                db.collection("events")
                    .document(event.id)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Event deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadEvents()

                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Failed to delete event",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            }
            .setNegativeButton("Cancel", null)
            .show()

    }

}