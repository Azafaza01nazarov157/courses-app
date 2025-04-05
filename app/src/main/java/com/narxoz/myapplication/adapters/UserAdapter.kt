package com.narxoz.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.User

class UserAdapter(
    private var users: List<User>,
    private val onEditClickListener: (User) -> Unit,
    private val onDeleteClickListener: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTextView: TextView = itemView.findViewById(R.id.tvUserFullName)
        val usernameTextView: TextView = itemView.findViewById(R.id.tvUsername)
        val emailTextView: TextView = itemView.findViewById(R.id.tvUserEmail)
        val roleTextView: TextView = itemView.findViewById(R.id.tvUserRole)
        val editButton: Button = itemView.findViewById(R.id.btnEditUser)
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        
        holder.fullNameTextView.text = user.fullName
        holder.usernameTextView.text = "Username: ${user.username}"
        holder.emailTextView.text = "Email: ${user.email ?: "N/A"}"
        holder.roleTextView.text = "Role: ${user.role.name}"
        
        holder.editButton.setOnClickListener { onEditClickListener(user) }
        holder.deleteButton.setOnClickListener { onDeleteClickListener(user) }
    }

    override fun getItemCount(): Int = users.size
    
    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
} 