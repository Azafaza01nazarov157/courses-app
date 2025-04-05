package com.narxoz.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.User

class EnrollmentAdapter(
    private var enrollments: List<Pair<User, String>>,
    private val onRemoveClickListener: (User) -> Unit
) : RecyclerView.Adapter<EnrollmentAdapter.EnrollmentViewHolder>() {

    inner class EnrollmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvEnrollmentName)
        val roleTextView: TextView = itemView.findViewById(R.id.tvEnrollmentRole)
        val removeButton: Button = itemView.findViewById(R.id.btnRemoveEnrollment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_enrollment, parent, false)
        return EnrollmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        val (user, role) = enrollments[position]
        
        holder.nameTextView.text = user.fullName
        holder.roleTextView.text = "Type: $role | Username: ${user.username}"
        
        holder.removeButton.setOnClickListener { onRemoveClickListener(user) }
    }

    override fun getItemCount(): Int = enrollments.size
    
    fun updateEnrollments(newEnrollments: List<Pair<User, String>>) {
        enrollments = newEnrollments
        notifyDataSetChanged()
    }
    
    fun getUserType(user: User): String {
        return enrollments.find { it.first.id == user.id }?.second ?: ""
    }
} 