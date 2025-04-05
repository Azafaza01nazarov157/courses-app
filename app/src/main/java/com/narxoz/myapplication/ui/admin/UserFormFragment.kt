package com.narxoz.myapplication.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.narxoz.myapplication.R
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.models.UserRole

class UserFormFragment : Fragment() {
    
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var roleSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    
    private lateinit var courseRepository: CourseRepository
    private var user: User? = null
    
    companion object {
        private const val ARG_USER_ID = "user_id"
        
        fun newInstance(userId: Long? = null): UserFormFragment {
            val fragment = UserFormFragment()
            userId?.let {
                val args = Bundle()
                args.putLong(ARG_USER_ID, it)
                fragment.arguments = args
            }
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseRepository = CourseRepository.getInstance(requireContext())
        
        arguments?.let {
            if (it.containsKey(ARG_USER_ID)) {
                val userId = it.getLong(ARG_USER_ID)
                user = courseRepository.getUserById(userId)
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_form, container, false)
        
        usernameEditText = view.findViewById(R.id.etUsername)
        passwordEditText = view.findViewById(R.id.etPassword)
        fullNameEditText = view.findViewById(R.id.etFullName)
        emailEditText = view.findViewById(R.id.etEmail)
        roleSpinner = view.findViewById(R.id.spinnerRole)
        saveButton = view.findViewById(R.id.btnSaveUser)
        cancelButton = view.findViewById(R.id.btnCancelUser)
        
        setupRoleSpinner()
        
        setupExistingUserData()
        
        saveButton.setOnClickListener { saveUser() }
        cancelButton.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            if (user == null) getString(R.string.add_new_user) else getString(R.string.edit_user)
    }
    
    private fun setupRoleSpinner() {
        val roles = UserRole.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
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
            // Password is required only for new users
            passwordEditText.error = "Password is required"
            return
        }
        
        val selectedRole = UserRole.valueOf(roleSpinner.selectedItem.toString())
        
        if (user == null) {
            // Create new user
            val newUser = User(
                username = username,
                password = password,
                role = selectedRole,
                fullName = fullName,
                email = email
            )
            
            val id = courseRepository.createUser(newUser)
            if (id > 0) {
                Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Failed to add user", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Update existing user
            val updatedUser = user!!.copy(
                username = username,
                password = if (password.isNotEmpty()) password else user!!.password,
                role = selectedRole,
                fullName = fullName,
                email = email
            )
            
            val result = courseRepository.updateUser(updatedUser)
            if (result > 0) {
                Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(context, "Failed to update user", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 