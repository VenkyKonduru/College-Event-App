package com.example.collegeeventapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

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
                                    startActivity(Intent(this,AdminDashboardActivity::class.java))
                                    finish()
                                }
                                else if(role == "Student"){
                                    startActivity(Intent(this,StudentDashboardActivity::class.java))
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