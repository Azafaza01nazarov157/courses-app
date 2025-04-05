package com.narxoz.myapplication.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.models.UserRole

class UserDialog(
    context: Context,
    private val user: User?,
    private val onSaveCallback: (String, String, UserRole, String, String?) -> Unit
) : Dialog(context) {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_user)
        
        usernameEditText = findViewById(R.id.etUsername)
        passwordEditText = findViewById(R.id.etPassword)
        fullNameEditText = findViewById(R.id.etFullName)
        emailEditText = findViewById(R.id.etEmail)
        roleSpinner = findViewById(R.id.spinnerRole)
        saveButton = findViewById(R.id.btnSaveUser)
        cancelButton = findViewById(R.id.btnCancelUser)
        
        setupRoleSpinner()
        
        setupExistingUserData()
        
        saveButton.setOnClickListener { saveUser() }
        cancelButton.setOnClickListener { dismiss() }
    }
    
    private fun setupRoleSpinner() {
        val roles = UserRole.values().map { it.name }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter
    }
    
    private fun setupExistingUserData() {
        user?.let {
            usernameEditText.setText(it.username)
            fullNameEditText.setText(it.fullName)
            emailEditText.setText(it.email)
            
            passwordEditText.hint = "Leave blank to keep current password"
            
            val rolePosition = UserRole.values().indexOf(it.role)
            if (rolePosition != -1) {
                roleSpinner.setSelection(rolePosition)
            }
        }
    }
    
    private fun saveUser() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val fullName = fullNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim().takeIf { it.isNotEmpty() }
        
        if (username.isEmpty()) {
            usernameEditText.error = "Username is required"
            return
        }
        
        if (fullName.isEmpty()) {
            fullNameEditText.error = "Full name is required"
            return
        }
        
        if (user == null && password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return
        }
        
        val selectedRole = UserRole.valueOf(roleSpinner.selectedItem.toString())
        
        onSaveCallback(username, password, selectedRole, fullName, email)
        dismiss()
    }
} 