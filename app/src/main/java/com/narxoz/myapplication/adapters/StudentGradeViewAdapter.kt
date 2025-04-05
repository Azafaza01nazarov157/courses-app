package com.narxoz.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.Assignment
import com.narxoz.myapplication.models.Grade

class StudentGradeViewAdapter(
    private var gradeItems: List<GradeItem>
) : RecyclerView.Adapter<StudentGradeViewAdapter.GradeViewHolder>() {

    data class GradeItem(
        val assignment: Assignment,
        val grade: Grade
    )

    inner class GradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assignmentTitleTextView: TextView = itemView.findViewById(R.id.tvAssignmentTitle)
        val gradedDateTextView: TextView = itemView.findViewById(R.id.tvGradedDate)
        val scoreTextView: TextView = itemView.findViewById(R.id.tvScore)
        val commentLabelTextView: TextView = itemView.findViewById(R.id.tvCommentLabel)
        val commentTextView: TextView = itemView.findViewById(R.id.tvComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student_grade_view, parent, false)
        return GradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        val gradeItem = gradeItems[position]
        
        holder.assignmentTitleTextView.text = "Assignment: ${gradeItem.assignment.title}"
        holder.gradedDateTextView.text = "Graded on: ${gradeItem.grade.gradedAt ?: "Unknown"}"
        holder.scoreTextView.text = gradeItem.grade.score.toString()
        
        if (!gradeItem.grade.comment.isNullOrEmpty()) {
            holder.commentLabelTextView.visibility = View.VISIBLE
            holder.commentTextView.visibility = View.VISIBLE
            holder.commentTextView.text = gradeItem.grade.comment
        } else {
            holder.commentLabelTextView.visibility = View.GONE
            holder.commentTextView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = gradeItems.size
    
    fun updateGrades(newGradeItems: List<GradeItem>) {
        gradeItems = newGradeItems
        notifyDataSetChanged()
    }
} 