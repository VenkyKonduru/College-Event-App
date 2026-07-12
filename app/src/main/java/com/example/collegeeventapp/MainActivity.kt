package com.example.collegeeventapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.SharedPreferences
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("CollegeEventPrefs", MODE_PRIVATE)

        if (sharedPreferences.getBoolean("isDarkMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {

            val role = sharedPreferences.getString("userRole", "")

            if (role == "Admin") {
                startActivity(Intent(this, AdminDashboardActivity::class.java))
                finish()
                return
            }

            if (role == "Student") {
                startActivity(Intent(this, StudentDashboardActivity::class.java))
                finish()
                return
            }
        }



        btnLogin.setOnClickListener {


            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if(email.isEmpty()) {
                Toast.makeText(this,"Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password.isEmpty()) {
                Toast.makeText(this,"Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,
                            "Login Successful",
                            Toast.LENGTH_SHORT)
                            .show()

                        val uid = auth.currentUser!!.uid

                        db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role")

                                if(role == "Admin"){

                                    sharedPreferences.edit()
                                        .putBoolean("isLoggedIn", true)
                                        .putString("userRole", "Admin")
                                        .apply()

                                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                                    finish()
                                }
                                else if(role == "Student"){

                                    sharedPreferences.edit()
                                        .putBoolean("isLoggedIn", true)
                                        .putString("userRole", "Student")
                                        .apply()

                                    startActivity(Intent(this, StudentDashboardActivity::class.java))
                                    finish()
                                }
                                else{
                                    Toast.makeText(this,
                                        "Invalid user role",
                                        Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,
                                    "Failed to fetch user data",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }



                    }

                    else{
                        Toast.makeText(this,
                            task.exception?.message,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }


        }

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}