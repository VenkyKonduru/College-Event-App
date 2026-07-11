package com.example.collegeeventapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class StudentHomeFragment : Fragment() {

    private lateinit var tvStudentName: TextView
    private lateinit var tvTotalEvents: TextView
    private lateinit var tvRegisteredEvents: TextView

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_student_home,
            container,
            false
        )

        tvStudentName = view.findViewById(R.id.tvStudentName)
        tvTotalEvents = view.findViewById(R.id.tvTotalEvents)
        tvRegisteredEvents = view.findViewById(R.id.tvRegisteredEvents)

        loadStudentName()
        loadTotalEvents()
        loadRegisteredEvents()

        return view
    }

    private fun loadStudentName() {

        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener {

                tvStudentName.text =
                    it.getString("name") ?: "Student"

            }
    }

    private fun loadTotalEvents() {

        db.collection("events")
            .get()
            .addOnSuccessListener {

                tvTotalEvents.text = it.size().toString()

            }
    }

    private fun loadRegisteredEvents() {

        val user = auth.currentUser ?: return

        db.collection("registrations")
            .whereEqualTo("studentUid", user.uid)
            .get()
            .addOnSuccessListener {

                tvRegisteredEvents.text = it.size().toString()

            }
    }
}