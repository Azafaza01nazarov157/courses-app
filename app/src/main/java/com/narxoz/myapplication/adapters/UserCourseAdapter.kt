package com.narxoz.myapplication.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.Course

class UserCourseAdapter(
    private var courses: List<Course>,
    private val onCourseClickListener: (Course) -> Unit
) : RecyclerView.Adapter<UserCourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tvCourseTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tvCourseDescription)
        val datesTextView: TextView = itemView.findViewById(R.id.tvCourseDates)
        val statusTextView: TextView = itemView.findViewById(R.id.tvCourseStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        
        holder.titleTextView.text = course.title
        holder.descriptionTextView.text = course.description ?: "No description available"
        
        val dateRange = buildDateRangeText(course.startDate, course.endDate)
        holder.datesTextView.text = dateRange
        
        if (course.isActive) {
            holder.statusTextView.text = "Active"
            holder.statusTextView.setBackgroundResource(R.drawable.status_background_active)
        } else {
            holder.statusTextView.text = "Inactive"
            holder.statusTextView.setBackgroundResource(R.drawable.status_background_inactive)
        }
        
        holder.itemView.setOnClickListener { onCourseClickListener(course) }
    }

    override fun getItemCount(): Int = courses.size
    
    fun updateCourses(newCourses: List<Course>) {
        courses = newCourses
        notifyDataSetChanged()
    }
    
    private fun buildDateRangeText(startDate: String?, endDate: String?): String {
        return when {
            startDate != null && endDate != null -> "From $startDate to $endDate"
            startDate != null -> "Starts on $startDate"
            endDate != null -> "Ends on $endDate"
            else -> "No dates specified"
        }
    }
} 