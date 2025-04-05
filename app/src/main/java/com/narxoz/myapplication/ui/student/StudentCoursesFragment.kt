package com.narxoz.myapplication.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.adapters.UserCourseAdapter
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.models.Course
import com.narxoz.myapplication.utils.SessionManager

class StudentCoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var courseAdapter: UserCourseAdapter
    
    private lateinit var courseRepository: CourseRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_courses, container, false)
        
        // Initialize repositories and session manager
        courseRepository = CourseRepository.getInstance(requireContext())
        sessionManager = SessionManager.getInstance(requireContext())

        // Initialize UI elements
        recyclerView = view.findViewById(R.id.recyclerViewStudentCourses)
        emptyView = view.findViewById(R.id.textViewEmptyCourses)
        
        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        courseAdapter = UserCourseAdapter(emptyList()) { course -> onCourseClick(course) }
        recyclerView.adapter = courseAdapter
        
        // Load student courses
        loadStudentCourses()
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        loadStudentCourses()
    }
    
    private fun loadStudentCourses() {
        val studentId = sessionManager.getUserId()
        val courses = courseRepository.getCoursesForStudent(studentId)
        
        if (courses.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            courseAdapter.updateCourses(courses)
        }
    }
    
    private fun onCourseClick(course: Course) {
        // For now, just show a toast with the course title
        // Later, this could navigate to a course detail view
        Toast.makeText(context, "Selected course: ${course.title}", Toast.LENGTH_SHORT).show()
    }
} 