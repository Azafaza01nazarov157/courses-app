package com.narxoz.myapplication.ui.teacher

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

class TeacherCoursesFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_teacher_courses, container, false)

        courseRepository = CourseRepository.getInstance(requireContext())
        sessionManager = SessionManager.getInstance(requireContext())

        recyclerView = view.findViewById(R.id.recyclerViewTeacherCourses)
        emptyView = view.findViewById(R.id.textViewEmptyCourses)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        courseAdapter = UserCourseAdapter(emptyList()) { course -> onCourseClick(course) }
        recyclerView.adapter = courseAdapter
        
        loadTeacherCourses()
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        loadTeacherCourses()
    }
    
    private fun loadTeacherCourses() {
        val teacherId = sessionManager.getUserId()
        val courses = courseRepository.getCoursesForTeacher(teacherId)
        
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
        Toast.makeText(context, "Selected course: ${course.title}", Toast.LENGTH_SHORT).show()
    }
} 