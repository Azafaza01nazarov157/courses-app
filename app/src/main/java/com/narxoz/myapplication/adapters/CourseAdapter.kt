package com.narxoz.myapplication.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.Course

class CourseAdapter(
    private var courses: List<Course>,
    private val onEditClickListener: (Course) -> Unit,
    private val onDeleteClickListener: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tvCourseTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tvCourseDescription)
        val datesTextView: TextView = itemView.findViewById(R.id.tvCourseDates)
        val editButton: Button = itemView.findViewById(R.id.btnEditCourse)
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteCourse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]
        
        holder.titleTextView.text = course.title
        holder.descriptionTextView.text = course.description ?: "No description available"
        
        val dateRange = buildDateRangeText(course.startDate, course.endDate)
        holder.datesTextView.text = dateRange
        
        holder.editButton.setOnClickListener { onEditClickListener(course) }
        holder.deleteButton.setOnClickListener { onDeleteClickListener(course) }
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