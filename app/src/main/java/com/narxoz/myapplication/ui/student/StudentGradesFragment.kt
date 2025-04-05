package com.narxoz.myapplication.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.adapters.StudentGradeViewAdapter
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.models.Course
import com.narxoz.myapplication.utils.SessionManager

class StudentGradesFragment : Fragment() {

    private lateinit var spinnerCourses: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var gradeAdapter: StudentGradeViewAdapter

    private lateinit var courseRepository: CourseRepository
    private lateinit var sessionManager: SessionManager

    private var courses: List<Course> = emptyList()
    private var selectedCourse: Course? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_grades, container, false)

        courseRepository = CourseRepository.getInstance(requireContext())
        sessionManager = SessionManager.getInstance(requireContext())

        spinnerCourses = view.findViewById(R.id.spinnerCourses)
        recyclerView = view.findViewById(R.id.recyclerViewGrades)
        emptyView = view.findViewById(R.id.textViewEmptyGrades)

        recyclerView.layoutManager = LinearLayoutManager(context)
        gradeAdapter = StudentGradeViewAdapter(emptyList())
        recyclerView.adapter = gradeAdapter

        setupCourseSpinner()

        loadStudentCourses()

        return view
    }

    private fun setupCourseSpinner() {
        spinnerCourses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (courses.isNotEmpty() && position < courses.size) {
                    selectedCourse = courses[position]
                    loadGradesForCourse(selectedCourse!!)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCourse = null
                updateGradesList(emptyList())
            }
        }
    }

    private fun loadStudentCourses() {
        val studentId = sessionManager.getUserId()
        courses = courseRepository.getCoursesForStudent(studentId)

        if (courses.isEmpty()) {
            showEmptyView("You are not enrolled in any courses")
            return
        }

        val courseNames = courses.map { it.title }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapter

        spinnerCourses.setSelection(0)
    }

    private fun loadGradesForCourse(course: Course) {
        val studentId = sessionManager.getUserId()
        val grades = courseRepository.getGradesForStudentInCourse(studentId, course.id)

        if (grades.isEmpty()) {
            showEmptyView("No grades available for this course")
            return
        }

        val gradeItems = grades.mapNotNull { grade ->
            val assignment = courseRepository.getAssignmentById(grade.assignmentId)
            if (assignment != null) {
                StudentGradeViewAdapter.GradeItem(assignment, grade)
            } else null
        }

        updateGradesList(gradeItems)
    }

    private fun updateGradesList(gradeItems: List<StudentGradeViewAdapter.GradeItem>) {
        if (gradeItems.isEmpty()) {
            showEmptyView("No grades to display")
        } else {
            hideEmptyView()
            gradeAdapter.updateGrades(gradeItems)
        }
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