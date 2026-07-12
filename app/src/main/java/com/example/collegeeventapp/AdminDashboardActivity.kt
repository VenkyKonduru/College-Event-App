package com.example.collegeeventapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences

class AdminDashboardActivity : AppCompatActivity() {


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: MaterialToolbar


    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

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

                    tvUserName.text = document.getString("name") ?: "Admin"

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


        if (savedInstanceState == null) {
            supportActionBar?.title = "Manage Events"
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .commit()
        }

        navigationView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_dashboard -> {
                    supportActionBar?.title = "Manage Events"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, DashboardFragment())
                        .commit()

                }

                R.id.nav_add_event -> {
                    supportActionBar?.title = "Add Event"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AddEventFragment())
                        .addToBackStack(null)
                        .commit()

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