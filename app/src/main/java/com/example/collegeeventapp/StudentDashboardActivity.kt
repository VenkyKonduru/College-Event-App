package com.example.collegeeventapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    private lateinit var cardAllEvents: MaterialCardView
    private lateinit var cardMyEvents: MaterialCardView

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_dashboard)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        val headerView = navigationView.getHeaderView(0)

        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = headerView.findViewById<TextView>(R.id.tvUserEmail)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {

            tvUserEmail.text = user.email

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->

                    tvUserName.text = document.getString("name") ?: "Atudent"

                }

        }


        toolbar = findViewById(R.id.toolbar)

        cardAllEvents = findViewById(R.id.cardAllEvents)
        cardMyEvents = findViewById(R.id.cardMyEvents)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Student Dashboard"

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Default Fragment
        if (savedInstanceState == null) {
            replaceFragment(AllEventsFragment())
            navigationView.setCheckedItem(R.id.nav_all_events)
        }

        // Card Clicks
        cardAllEvents.setOnClickListener {
            replaceFragment(AllEventsFragment())
            navigationView.setCheckedItem(R.id.nav_all_events)
        }

        cardMyEvents.setOnClickListener {
            replaceFragment(MyEventsFragment())
            navigationView.setCheckedItem(R.id.nav_my_events)
        }

        // Drawer Clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.nav_all_events -> {
                    replaceFragment(AllEventsFragment())
                }

                R.id.nav_my_events -> {
                    replaceFragment(MyEventsFragment())
                }

                R.id.nav_logout -> {
                    auth.signOut()

                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    finish()
                }
            }

            drawerLayout.closeDrawers()
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}