package com.example.collegeeventapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddEventFragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    private var isEdit = false
    private var eventId = ""

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvSubtitle: TextView

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etDate: TextInputEditText
    private lateinit var etVenue: TextInputEditText

    private lateinit var btnAddEvent: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_add_event,
            container,
            false
        )

        db = FirebaseFirestore.getInstance()

        toolbar = view.findViewById(R.id.toolbarAddEvent)
        tvSubtitle = view.findViewById(R.id.tvSubtitle)

        etTitle = view.findViewById(R.id.etTitle)
        etDescription = view.findViewById(R.id.etDescription)
        etDate = view.findViewById(R.id.etDate)
        etDate.keyListener = null
        etVenue = view.findViewById(R.id.etVenue)
        btnAddEvent = view.findViewById(R.id.btnAddEvent)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        isEdit = arguments?.getBoolean("isEdit", false) ?: false

        if (isEdit) {

            eventId = arguments?.getString("eventId") ?: ""

            etTitle.setText(arguments?.getString("title"))
            etDescription.setText(arguments?.getString("description"))
            etDate.setText(arguments?.getString("date"))
            etVenue.setText(arguments?.getString("venue"))

            toolbar.title = "Edit Event"
            tvSubtitle.text = "Modify the event information"
            btnAddEvent.text = "UPDATE EVENT"
        } else {

            toolbar.title = "Add New Event"
            tvSubtitle.text = "Fill in the event details below"
            btnAddEvent.text = "ADD EVENT"
        }

        etDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->

                    etDate.setText(
                        String.format(
                            "%02d-%02d-%04d",
                            day,
                            month + 1,
                            year
                        )
                    )

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }

        btnAddEvent.setOnClickListener {
            saveEvent()
        }

        return view
    }

    private fun saveEvent() {

        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val date = etDate.text.toString().trim()
        val venue = etVenue.text.toString().trim()

        if (title.isEmpty()) {
            etTitle.error = "Required"
            return
        }

        if (description.isEmpty()) {
            etDescription.error = "Required"
            return
        }

        if (date.isEmpty()) {
            etDate.error = "Required"
            return
        }

        if (venue.isEmpty()) {
            etVenue.error = "Required"
            return
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
                        requireContext(),
                        "Event updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    parentFragmentManager.popBackStack()

                }
                .addOnFailureListener {

                    Toast.makeText(
                        requireContext(),
                        "Failed to update event",
                        Toast.LENGTH_SHORT
                    ).show()

                }

        } else {

            db.collection("events")
                .add(event)
                .addOnSuccessListener {

                    Toast.makeText(
                        requireContext(),
                        "Event added successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    etTitle.text?.clear()
                    etDescription.text?.clear()
                    etDate.text?.clear()
                    etVenue.text?.clear()

                    etTitle.requestFocus()

                }
                .addOnFailureListener {

                    Toast.makeText(
                        requireContext(),
                        "Failed to add event",
                        Toast.LENGTH_SHORT
                    ).show()

                }

        }
    }
}