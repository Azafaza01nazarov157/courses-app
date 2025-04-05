package com.narxoz.myapplication.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.narxoz.myapplication.R
import com.narxoz.myapplication.models.User

class EnrollmentDialog(
    context: Context,
    private val title: String,
    private val users: List<User>,
    private val onSelectCallback: (User) -> Unit
) : Dialog(context) {

    private lateinit var titleTextView: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var selectButton: Button
    private lateinit var cancelButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_enrollment)
        
        titleTextView = findViewById(R.id.tvEnrollmentDialogTitle)
        radioGroup = findViewById(R.id.radioGroupUsers)
        selectButton = findViewById(R.id.btnSelectUser)
        cancelButton = findViewById(R.id.btnCancelEnrollment)
        
        titleTextView.text = title

        populateRadioGroup()
        
        selectButton.setOnClickListener { selectUser() }
        cancelButton.setOnClickListener { dismiss() }
    }
    
    private fun populateRadioGroup() {
        users.forEachIndexed { index, user ->
            val radioButton = RadioButton(context)
            radioButton.id = index
            radioButton.text = "${user.fullName} (${user.username})"
            radioGroup.addView(radioButton)
            
            if (index == 0) {
                radioButton.isChecked = true
            }
        }
    }
    
    private fun selectUser() {
        val selectedId = radioGroup.checkedRadioButtonId
        if (selectedId != -1 && selectedId < users.size) {
            val selectedUser = users[selectedId]
            onSelectCallback(selectedUser)
            dismiss()
        }
    }
} 