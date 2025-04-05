package com.narxoz.myapplication.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.Course
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CourseDialog(
    context: Context,
    private val course: Course?,
    private val onSaveCallback: (String, String?, String?, String?) -> Unit,
    private val fragmentManager: androidx.fragment.app.FragmentManager? = (context as? FragmentActivity)?.supportFragmentManager
) : Dialog(context) {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var startDateEditText: EditText
    private lateinit var endDateEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    
    private var startDate: String? = null
    private var endDate: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_course)
        
        titleEditText = findViewById(R.id.etCourseTitle)
        descriptionEditText = findViewById(R.id.etCourseDescription)
        startDateEditText = findViewById(R.id.etStartDate)
        endDateEditText = findViewById(R.id.etEndDate)
        saveButton = findViewById(R.id.btnSaveCourse)
        cancelButton = findViewById(R.id.btnCancelCourse)
        
        course?.let {
            titleEditText.setText(it.title)
            descriptionEditText.setText(it.description)
            startDateEditText.setText(it.startDate)
            endDateEditText.setText(it.endDate)
            startDate = it.startDate
            endDate = it.endDate
        }
        
        setupDatePickers()
        
        saveButton.setOnClickListener { saveCourse() }
        cancelButton.setOnClickListener { dismiss() }
    }
    
    private fun setupDatePickers() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        startDateEditText.setOnClickListener {
            if (fragmentManager == null) {
                startDateEditText.isEnabled = false
                return@setOnClickListener
            }
            
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select start date")
                .build()
                
            datePicker.addOnPositiveButtonClickListener { selection ->
                val date = Date(selection)
                startDate = dateFormat.format(date)
                startDateEditText.setText(startDate)
            }
            
            datePicker.show(fragmentManager, "START_DATE_PICKER")
        }
        
        endDateEditText.setOnClickListener {
            if (fragmentManager == null) {
                endDateEditText.isEnabled = false
                return@setOnClickListener
            }
            
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select end date")
                .build()
                
            datePicker.addOnPositiveButtonClickListener { selection ->
                val date = Date(selection)
                endDate = dateFormat.format(date)
                endDateEditText.setText(endDate)
            }
            
            datePicker.show(fragmentManager, "END_DATE_PICKER")
        }
    }
    
    private fun saveCourse() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim().takeIf { it.isNotEmpty() }
        
        if (title.isEmpty()) {
            titleEditText.error = "Title is required"
            return
        }
        
        onSaveCallback(title, description, startDate, endDate)
        dismiss()
    }
} 