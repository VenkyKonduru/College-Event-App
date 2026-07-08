package com.example.collegeeventapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore




class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter
    private lateinit var tvNoEvents: TextView

    private val eventList = ArrayList<Event>()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_student_dashboard)

        tvNoEvents = findViewById(R.id.tvNoEvents)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = EventAdapter(eventList)
        recyclerView.adapter = adapter

        loadEvents()

    }

    private fun loadEvents(){
        eventList.clear()
        db.collection("events")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val event = document.toObject(Event::class.java)
                    eventList.add(event)

                }
                adapter.notifyDataSetChanged()
                updateUI()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this,
                    e.message ?: "Unknown error occurred",
                    Toast.LENGTH_SHORT).show()
                updateUI()
            }
    }

    private fun updateUI(){
        if(eventList.isEmpty()){
            tvNoEvents.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
        else{
            tvNoEvents.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}