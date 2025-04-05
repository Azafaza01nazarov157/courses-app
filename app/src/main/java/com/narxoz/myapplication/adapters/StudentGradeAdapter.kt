package com.narxoz.myapplication.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.Assignment
import com.narxoz.myapplication.models.Grade
import com.narxoz.myapplication.models.User

class StudentGradeAdapter(
    private var students: List<Student>,
    private val onGradeSaved: (Student, Double, String?) -> Unit
) : RecyclerView.Adapter<StudentGradeAdapter.StudentViewHolder>() {

    data class Student(
        val user: User,
        val existingGrade: Grade?
    )

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentNameTextView: TextView = itemView.findViewById(R.id.tvStudentName)
        val currentGradeTextView: TextView = itemView.findViewById(R.id.tvCurrentGrade)
        val gradeEditText: EditText = itemView.findViewById(R.id.etGrade)
        val commentEditText: EditText = itemView.findViewById(R.id.etComment)
        val saveButton: Button = itemView.findViewById(R.id.btnSaveGrade)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_grade, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        
        holder.studentNameTextView.text = student.user.fullName
        
        if (student.existingGrade != null) {
            holder.currentGradeTextView.visibility = View.VISIBLE
            holder.currentGradeTextView.text = "Current Grade: ${student.existingGrade.score}"
            
            holder.gradeEditText.setText(student.existingGrade.score.toString())
            holder.commentEditText.setText(student.existingGrade.comment ?: "")
        } else {
            holder.currentGradeTextView.visibility = View.GONE
            holder.gradeEditText.text = null
            holder.commentEditText.text = null
        }
        
        holder.saveButton.setOnClickListener {
            val gradeStr = holder.gradeEditText.text.toString().trim()
            if (TextUtils.isEmpty(gradeStr)) {
                Toast.makeText(holder.itemView.context, "Please enter a grade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            try {
                val grade = gradeStr.toDouble()
                if (grade < 0 || grade > 100) {
                    Toast.makeText(holder.itemView.context, "Grade must be between 0 and 100", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                val comment = holder.commentEditText.text.toString().trim().takeIf { it.isNotEmpty() }
                onGradeSaved(student, grade, comment)
                Toast.makeText(holder.itemView.context, "Grade saved", Toast.LENGTH_SHORT).show()
            } catch (e: NumberFormatException) {
                Toast.makeText(holder.itemView.context, "Invalid grade format", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = students.size
    
    fun updateStudents(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }
} 