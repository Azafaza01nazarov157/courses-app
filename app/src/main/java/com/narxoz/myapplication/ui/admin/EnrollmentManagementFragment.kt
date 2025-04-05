package com.narxoz.myapplication.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.adapters.EnrollmentAdapter
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.dialogs.EnrollmentDialog
import com.narxoz.myapplication.models.Course
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.models.UserRole
import com.narxoz.myapplication.utils.SessionManager

class EnrollmentManagementFragment : Fragment() {

    private lateinit var spinnerCourses: Spinner
    private lateinit var recyclerViewEnrollments: RecyclerView
    private lateinit var enrollmentAdapter: EnrollmentAdapter
    private lateinit var btnAddTeacher: Button
    private lateinit var btnAddStudent: Button
    
    private lateinit var courseRepository: CourseRepository
    private lateinit var selectedCourse: Course
    private var courses: List<Course> = emptyList()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_enrollment_management, container, false)
        
        courseRepository = CourseRepository.getInstance(requireContext())
        
        spinnerCourses = view.findViewById(R.id.spinnerCourses)
        recyclerViewEnrollments = view.findViewById(R.id.recyclerViewEnrollments)
        btnAddTeacher = view.findViewById(R.id.btnAddTeacher)
        btnAddStudent = view.findViewById(R.id.btnAddStudent)
        
        recyclerViewEnrollments.layoutManager = LinearLayoutManager(context)
        enrollmentAdapter = EnrollmentAdapter(emptyList()) { user -> onRemoveEnrollment(user) }
        recyclerViewEnrollments.adapter = enrollmentAdapter
        
        loadCourses()
        
        setupCourseSpinner()
        
        btnAddTeacher.setOnClickListener { showAddTeacherDialog() }
        btnAddStudent.setOnClickListener { showAddStudentDialog() }
        
        return view
    }
    
    private fun loadCourses() {
        courses = courseRepository.getAllCourses()
        
        if (courses.isNotEmpty()) {
            val courseNames = courses.map { it.title }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCourses.adapter = adapter
            
            selectedCourse = courses[0]
            loadEnrollments()
        } else {
            Toast.makeText(context, "No courses available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupCourseSpinner() {
        spinnerCourses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCourse = courses[position]
                loadEnrollments()
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
    
    private fun loadEnrollments() {
        val teachers = courseRepository.getTeachersForCourse(selectedCourse.id)
        val students = courseRepository.getStudentsForCourse(selectedCourse.id)
        
        val enrollments = teachers.map { it to "Teacher" } + students.map { it to "Student" }
        enrollmentAdapter.updateEnrollments(enrollments)
    }
    
    private fun showAddTeacherDialog() {
        val teachers = courseRepository.getUsersByRole(UserRole.TEACHER)
        if (teachers.isEmpty()) {
            Toast.makeText(context, "No teachers available to add", Toast.LENGTH_SHORT).show()
            return
        }
        
        val dialog = EnrollmentDialog(
            requireContext(),
            "Add Teacher to Course",
            teachers
        ) { user ->
            val id = courseRepository.assignTeacherToCourse(selectedCourse.id, user.id)
            if (id > 0) {
                Toast.makeText(context, "Teacher added to course", Toast.LENGTH_SHORT).show()
                loadEnrollments()
            } else {
                Toast.makeText(context, "Failed to add teacher to course", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    
    private fun showAddStudentDialog() {
        val students = courseRepository.getUsersByRole(UserRole.STUDENT)
        if (students.isEmpty()) {
            Toast.makeText(context, "No students available to add", Toast.LENGTH_SHORT).show()
            return
        }
        
        val dialog = EnrollmentDialog(
            requireContext(),
            "Add Student to Course",
            students
        ) { user ->
            val id = courseRepository.enrollStudentInCourse(selectedCourse.id, user.id)
            if (id > 0) {
                Toast.makeText(context, "Student enrolled in course", Toast.LENGTH_SHORT).show()
                loadEnrollments()
            } else {
                Toast.makeText(context, "Failed to enroll student in course", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    
    private fun onRemoveEnrollment(user: User) {
        val userType = enrollmentAdapter.getUserType(user)
        
        when (userType) {
            "Teacher" -> {
                val result = courseRepository.removeTeacherFromCourse(selectedCourse.id, user.id)
                if (result > 0) {
                    Toast.makeText(context, "Teacher removed from course", Toast.LENGTH_SHORT).show()
                    loadEnrollments()
                } else {
                    Toast.makeText(context, "Failed to remove teacher from course", Toast.LENGTH_SHORT).show()
                }
            }
            "Student" -> {
                val result = courseRepository.removeStudentFromCourse(selectedCourse.id, user.id)
                if (result > 0) {
                    Toast.makeText(context, "Student removed from course", Toast.LENGTH_SHORT).show()
                    loadEnrollments()
                } else {
                    Toast.makeText(context, "Failed to remove student from course", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
} 