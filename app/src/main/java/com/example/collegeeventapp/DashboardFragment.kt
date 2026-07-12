package com.example.collegeeventapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment(),
    AdminEventAdapter.OnEventActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminEventAdapter
    private lateinit var noEventAdded: TextView

    private val db = FirebaseFirestore.getInstance()
    private val eventList = ArrayList<Event>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_dashboard,
            container,
            false
        )

        recyclerView = view.findViewById(R.id.rvEvents)
        noEventAdded = view.findViewById(R.id.noEventAdded)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = AdminEventAdapter(eventList, this)

        recyclerView.adapter = adapter

        view.findViewById<View>(R.id.fabAddEvent).setOnClickListener {
            onAddEvent()
        }

        loadEvents()

        return view
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

                android.widget.Toast.makeText(
                    requireContext(),
                    "Failed to load events",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

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

        val fragment = AddEventFragment()

        val bundle = Bundle()

        bundle.putBoolean("isEdit", true)
        bundle.putString("eventId", event.id)
        bundle.putString("title", event.title)
        bundle.putString("description", event.description)
        bundle.putString("date", event.date)
        bundle.putString("venue", event.venue)

        fragment.arguments = bundle

        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Edit Event"

        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()

    }

    override fun onDelete(event: Event) {

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event?")
            .setPositiveButton("Delete") { _, _ ->

                db.collection("events")
                    .document(event.id)
                    .delete()
                    .addOnSuccessListener {

                        android.widget.Toast.makeText(
                            requireContext(),
                            "Event deleted successfully",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                        loadEvents()

                    }
                    .addOnFailureListener {

                        android.widget.Toast.makeText(
                            requireContext(),
                            "Failed to delete event",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()

                    }

            }
            .setNegativeButton("Cancel", null)
            .show()

    }


    override fun onRegistrations(event: Event) {

        val fragment = RegisteredStudentsFragment()

        val bundle = Bundle()
        bundle.putString("eventId", event.id)

        fragment.arguments = bundle

        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Student Registrations"

        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()

    }

    private fun onAddEvent() {
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = "Add Event"
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, AddEventFragment())
            .addToBackStack(null)
            .commit()
    }

}