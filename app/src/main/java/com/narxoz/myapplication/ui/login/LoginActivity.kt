package com.narxoz.myapplication.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.narxoz.myapplication.MainActivity
import com.narxoz.myapplication.R
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.utils.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: AppCompatEditText
    private lateinit var passwordEditText: AppCompatEditText
    private lateinit var loginButton: AppCompatButton
    
    private lateinit var courseRepository: CourseRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        courseRepository = CourseRepository.getInstance(this)
        sessionManager = SessionManager.getInstance(this)
        
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity()
            return
        }
        
        usernameEditText = findViewById(R.id.etUsername)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val user = courseRepository.authenticateUser(username, password)
            
            if (user != null) {
                sessionManager.createSession(user.id, user.username, user.fullName, user.role)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToMainActivity()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
} 