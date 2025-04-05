package com.narxoz.myapplication.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.narxoz.myapplication.R
import com.narxoz.myapplication.data.CourseRepository
import com.narxoz.myapplication.utils.SessionManager

class StudentAssignmentsFragment : Fragment() {

    private lateinit var courseRepository: CourseRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_assignments, container, false)
        
        courseRepository = CourseRepository.getInstance(requireContext())
        sessionManager = SessionManager.getInstance(requireContext())
        
        val textView = view.findViewById<TextView>(R.id.textViewStudentAssignmentsPlaceholder)
        textView.text = "Student Assignments - Implementation pending"
        
        return view
    }
} 