package com.narxoz.myapplication.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.narxoz.myapplication.R
import com.narxoz.myapplication.adapters.UserAdapter
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.models.UserRole
import com.narxoz.myapplication.utils.SessionManager

class UserManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var fabAddUser: FloatingActionButton
    
    private lateinit var courseRepository: CourseRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_management, container, false)
        
        courseRepository = CourseRepository.getInstance(requireContext())
        sessionManager = SessionManager.getInstance(requireContext())
        
        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        fabAddUser = view.findViewById(R.id.fabAddUser)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        userAdapter = UserAdapter(emptyList(), { user -> onUserEdit(user) }, { user -> onUserDelete(user) })
        recyclerView.adapter = userAdapter
        
        fabAddUser.setOnClickListener {
            showAddUserForm()
        }
        
        loadUsers()
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        loadUsers()
    }
    
    private fun loadUsers() {
        val users = courseRepository.getAllUsers()
        userAdapter.updateUsers(users)
    }
    
    private fun showAddUserForm() {
        val formFragment = UserFormFragment.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, formFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun onUserEdit(user: User) {
        val formFragment = UserFormFragment.newInstance(user.id)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, formFragment)
            .addToBackStack(null)
            .commit()
    }
    
    private fun onUserDelete(user: User) {
        if (user.id == sessionManager.getUserId()) {
            Toast.makeText(context, "Cannot delete currently logged in user", Toast.LENGTH_SHORT).show()
            return
        }
        
        val result = courseRepository.deleteUser(user.id)
        if (result > 0) {
            Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show()
            loadUsers()
        } else {
            Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show()
        }
    }
} 