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
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.FirebaseFirestore

class RegisteredStudentsFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoStudents: TextView

    private lateinit var adapter: RegisteredStudentsAdapter

    private val db = FirebaseFirestore.getInstance()
    private val studentList = ArrayList<Registration>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_registered_students,
            container,
            false
        )

        toolbar = view.findViewById(R.id.toolbarRegisteredStudents)
        recyclerView = view.findViewById(R.id.rvRegisteredStudents)
        tvNoStudents = view.findViewById(R.id.tvNoStudents)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RegisteredStudentsAdapter(studentList)
        recyclerView.adapter = adapter

        loadStudents()

        return view
    }

    private fun loadStudents() {

        val eventId = arguments?.getString("eventId") ?: return

        db.collection("registrations")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { documents ->

                studentList.clear()

                for (document in documents) {

                    val registration =
                        document.toObject(Registration::class.java)

                    studentList.add(registration)

                }

                adapter.notifyDataSetChanged()

                updateUI()

            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    requireContext(),
                    e.message ?: "Failed to load students",
                    Toast.LENGTH_SHORT
                ).show()

            }

    }

    private fun updateUI() {

        if (studentList.isEmpty()) {

            tvNoStudents.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        } else {

            tvNoStudents.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

        }

    }

}