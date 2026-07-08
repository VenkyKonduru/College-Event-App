package com.example.collegeeventapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AddEventActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private var isEdit = false
    private var eventId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_event)

        db = FirebaseFirestore.getInstance()

        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etVenue = findViewById<EditText>(R.id.etVenue)
        val btnAddEvent = findViewById<Button>(R.id.btnAddEvent)

        // Check whether we are editing an existing event
        isEdit = intent.getBooleanExtra("isEdit", false)

        if (isEdit) {

            eventId = intent.getStringExtra("eventId") ?: ""

            etTitle.setText(intent.getStringExtra("title"))
            etDescription.setText(intent.getStringExtra("description"))
            etDate.setText(intent.getStringExtra("date"))
            etVenue.setText(intent.getStringExtra("venue"))

            btnAddEvent.text = "Update Event"
        }

        btnAddEvent.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val date = etDate.text.toString().trim()
            val venue = etVenue.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Title is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Description is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date.isEmpty()) {
                Toast.makeText(this, "Date is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (venue.isEmpty()) {
                Toast.makeText(this, "Venue is required.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val event = Event(
                title = title,
                description = description,
                date = date,
                venue = venue
            )

            if (isEdit) {

                db.collection("events")
                    .document(eventId)
                    .set(event)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Event updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    }
                    .addOnFailureListener { e ->

                        Toast.makeText(
                            this,
                            e.message ?: "Unknown error occurred",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            } else {

                db.collection("events")
                    .add(event)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Event added successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        etTitle.text.clear()
                        etDescription.text.clear()
                        etDate.text.clear()
                        etVenue.text.clear()

                    }
                    .addOnFailureListener { e ->

                        Toast.makeText(
                            this,
                            e.message ?: "Unknown error occurred",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            }
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
}