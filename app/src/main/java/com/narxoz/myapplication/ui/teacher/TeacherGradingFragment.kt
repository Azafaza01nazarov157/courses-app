package com.narxoz.myapplication.ui.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.adapters.StudentGradeAdapter
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.models.Assignment
import com.narxoz.myapplication.models.Course
import com.narxoz.myapplication.models.Grade
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeacherGradingFragment : Fragment() {

    private lateinit var spinnerCourses: Spinner
    private lateinit var spinnerAssignments: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    
    private lateinit var courseRepository: CourseRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var gradeAdapter: StudentGradeAdapter
    
    private var courses: List<Course> = emptyList()
    private var assignments: List<Assignment> = emptyList()
    private var selectedCourse: Course? = null
    private var selectedAssignment: Assignment? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_grading, container, false)
        
        courseRepository = CourseRepository.getInstance(requireContext())
        sessionManager = SessionManager.getInstance(requireContext())
        
        spinnerCourses = view.findViewById(R.id.spinnerCourses)
        spinnerAssignments = view.findViewById(R.id.spinnerAssignments)
        recyclerView = view.findViewById(R.id.recyclerViewStudents)
        emptyView = view.findViewById(R.id.textViewEmptyMessage)
        
        recyclerView.layoutManager = LinearLayoutManager(context)
        gradeAdapter = StudentGradeAdapter(emptyList()) { student, score, comment ->
            saveGrade(student.user, score, comment)
        }
        recyclerView.adapter = gradeAdapter
        
        setupSpinners()
        
        loadTeacherCourses()
        
        return view
    }
    
    private fun setupSpinners() {
        spinnerCourses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (courses.isNotEmpty() && position < courses.size) {
                    selectedCourse = courses[position]
                    loadAssignmentsForCourse(selectedCourse!!)
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCourse = null
                loadAssignmentsForCourse(null)
            }
        }
        
        spinnerAssignments.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (assignments.isNotEmpty() && position < assignments.size) {
                    selectedAssignment = assignments[position]
                    loadStudentsForAssignment()
                }
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedAssignment = null
                updateStudentList(emptyList())
            }
        }
    }
    
    private fun loadTeacherCourses() {
        val teacherId = sessionManager.getUserId()
        courses = courseRepository.getCoursesForTeacher(teacherId)
        
        if (courses.isEmpty()) {
            showEmptyView("You are not assigned to any courses")
            return
        }
        
        val courseNames = courses.map { it.title }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapter
        
        spinnerCourses.setSelection(0)
    }
    
    private fun loadAssignmentsForCourse(course: Course?) {
        if (course == null) {
            assignments = emptyList()
            updateAssignmentSpinner(emptyList())
            showEmptyView("No course selected")
            return
        }
        
        assignments = courseRepository.getAssignmentsForCourse(course.id)
        
        if (assignments.isEmpty()) {
            showEmptyView("No assignments available for this course")
            updateAssignmentSpinner(emptyList())
            return
        }
        
        updateAssignmentSpinner(assignments)
        
        spinnerAssignments.setSelection(0)
    }
    
    private fun updateAssignmentSpinner(assignments: List<Assignment>) {
        val assignmentNames = assignments.map { it.title }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, assignmentNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAssignments.adapter = adapter
    }
    
    private fun loadStudentsForAssignment() {
        if (selectedCourse == null || selectedAssignment == null) {
            updateStudentList(emptyList())
            return
        }
        
        val students = courseRepository.getStudentsForCourse(selectedCourse!!.id)
        
        if (students.isEmpty()) {
            showEmptyView("No students enrolled in this course")
            return
        }
        
        val studentsWithGrades = students.map { student ->
            val existingGrade = courseRepository.getGradeForAssignmentAndStudent(
                selectedAssignment!!.id, student.id
            )
            StudentGradeAdapter.Student(student, existingGrade)
        }
        
        updateStudentList(studentsWithGrades)
    }
    
    private fun updateStudentList(students: List<StudentGradeAdapter.Student>) {
        if (students.isEmpty()) {
            showEmptyView("No students to grade")
        } else {
            hideEmptyView()
            gradeAdapter.updateStudents(students)
        }
    }
    
    private fun saveGrade(student: User, score: Double, comment: String?) {
        if (selectedAssignment == null) {
            Toast.makeText(context, "No assignment selected", Toast.LENGTH_SHORT).show()
            return
        }
        
        val teacherId = sessionManager.getUserId()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        
        val existingGrade = courseRepository.getGradeForAssignmentAndStudent(
            selectedAssignment!!.id, student.id
        )
        
        if (existingGrade != null) {
            val updatedGrade = existingGrade.copy(
                score = score.toFloat(),
                comment = comment,
                gradedAt = currentDate,
                gradedBy = teacherId
            )
            
            val result = courseRepository.updateGrade(updatedGrade)
            if (result <= 0) {
                Toast.makeText(context, "Failed to update grade", Toast.LENGTH_SHORT).show()
            }
        } else {
            val newGrade = Grade(
                assignmentId = selectedAssignment!!.id,
                studentId = student.id,
                score = score.toFloat(),
                comment = comment,
                gradedAt = currentDate,
                gradedBy = teacherId
            )
            
            val id = courseRepository.createGrade(newGrade)
            if (id <= 0) {
                Toast.makeText(context, "Failed to save grade", Toast.LENGTH_SHORT).show()
            }
        }
        
        loadStudentsForAssignment()
    }
    
    private fun showEmptyView(message: String) {
        emptyView.text = message
        emptyView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
    
    private fun hideEmptyView() {
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
} 