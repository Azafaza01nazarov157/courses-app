package com.narxoz.myapplication.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.narxoz.myapplication.R
import com.narxoz.myapplication.adapters.CourseAdapter
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.dialogs.CourseDialog
import com.narxoz.myapplication.models.Course
import com.narxoz.myapplication.utils.SessionManager

class CourseManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var fabAddCourse: FloatingActionButton
    
    private lateinit var courseRepository: CourseRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_course_management, container, false)
        
        courseRepository = CourseRepository.getInstance(requireContext())
        sessionManager = SessionManager.getInstance(requireContext())
        
        recyclerView = view.findViewById(R.id.recyclerViewCourses)
        fabAddCourse = view.findViewById(R.id.fabAddCourse)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        courseAdapter = CourseAdapter(emptyList(), { course -> onCourseEdit(course) }, { course -> onCourseDelete(course) })
        recyclerView.adapter = courseAdapter
        
        fabAddCourse.setOnClickListener {
            showAddCourseDialog()
        }
        
        loadCourses()
        
        return view
    }
    
    private fun loadCourses() {
        val courses = courseRepository.getAllCourses()
        courseAdapter.updateCourses(courses)
    }
    
    private fun showAddCourseDialog() {
        val dialog = CourseDialog(
            requireContext(), 
            null, 
            { title, description, startDate, endDate ->
                val userId = sessionManager.getUserId()
                val course = Course(
                    title = title,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    createdBy = userId
                )
                
                val id = courseRepository.createCourse(course)
                if (id > 0) {
                    Toast.makeText(context, "Course added successfully", Toast.LENGTH_SHORT).show()
                    loadCourses()
                } else {
                    Toast.makeText(context, "Failed to add course", Toast.LENGTH_SHORT).show()
                }
            },
            childFragmentManager
        )
        dialog.show()
    }
    
    private fun onCourseEdit(course: Course) {
        val dialog = CourseDialog(
            requireContext(), 
            course, 
            { title, description, startDate, endDate ->
                val updatedCourse = course.copy(
                    title = title,
                    description = description,
                    startDate = startDate,
                    endDate = endDate
                )
                
                val result = courseRepository.updateCourse(updatedCourse)
                if (result > 0) {
                    Toast.makeText(context, "Course updated successfully", Toast.LENGTH_SHORT).show()
                    loadCourses()
                } else {
                    Toast.makeText(context, "Failed to update course", Toast.LENGTH_SHORT).show()
                }
            },
            childFragmentManager
        )
        dialog.show()
    }
    
    private fun onCourseDelete(course: Course) {
        val result = courseRepository.deleteCourse(course.id)
        if (result > 0) {
            Toast.makeText(context, "Course deleted successfully", Toast.LENGTH_SHORT).show()
            loadCourses()
        } else {
            Toast.makeText(context, "Failed to delete course", Toast.LENGTH_SHORT).show()
        }
    }
} 