package com.example.collegeeventapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar



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

                    tvUserName.text = document.getString("name") ?: "Student"

                }

        }


        toolbar = findViewById(R.id.toolbar)



        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

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
            supportActionBar?.title = "Home"
            replaceFragment(StudentHomeFragment())
            navigationView.setCheckedItem(R.id.nav_dashboard)
        }



        // Drawer Clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.nav_dashboard -> {
                    supportActionBar?.title = "Home"
                    replaceFragment(StudentHomeFragment())
                }

                R.id.nav_all_events -> {
                    supportActionBar?.title = "All Events"
                    replaceFragment(AllEventsFragment())
                }

                R.id.nav_my_events -> {
                    supportActionBar?.title = "My Events"
                    replaceFragment(MyEventsFragment())
                }

                R.id.nav_theme -> {
                    toggleDarkMode()
                }

                R.id.nav_logout -> {
                    auth.signOut()

                    val sharedPreferences =
                        getSharedPreferences("CollegeEventPrefs", Context.MODE_PRIVATE)

                    sharedPreferences.edit().clear().apply()

                    startActivity(Intent(this, MainActivity::class.java))
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

    private fun toggleDarkMode() {
        val sharedPrefs = getSharedPreferences("CollegeEventPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPrefs.getBoolean("isDarkMode", false)
        
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            sharedPrefs.edit().putBoolean("isDarkMode", false).apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            sharedPrefs.edit().putBoolean("isDarkMode", true).apply()
        }
    }
}