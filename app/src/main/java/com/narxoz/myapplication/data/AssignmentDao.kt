package com.narxoz.myapplication.data

import android.content.ContentValues
import android.database.Cursor
import com.narxoz.myapplication.models.Assignment

class AssignmentDao(private val database: AppDatabase) {

    fun createAssignment(assignment: Assignment): Long {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_COURSE_ID, assignment.courseId)
            put(AppDatabase.COLUMN_TITLE, assignment.title)
            put(AppDatabase.COLUMN_DESCRIPTION, assignment.description)
            put(AppDatabase.COLUMN_DEADLINE, assignment.deadline)
            put(AppDatabase.COLUMN_IS_ACTIVE, if (assignment.isActive) 1 else 0)
            put(AppDatabase.COLUMN_CREATED_BY, assignment.createdBy)
        }
        
        return db.insert(AppDatabase.TABLE_ASSIGNMENTS, null, values)
    }
    
    fun getAssignmentById(id: Long): Assignment? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_ASSIGNMENTS,
            null,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val assignment = cursorToAssignment(cursor)
            cursor.close()
            assignment
        } else {
            cursor.close()
            null
        }
    }
    
    fun getAssignmentsForCourse(courseId: Long, activeOnly: Boolean = false): List<Assignment> {
        val assignments = mutableListOf<Assignment>()
        val db = database.readableDatabase
        
        val selection = if (activeOnly) {
            "${AppDatabase.COLUMN_COURSE_ID} = ? AND ${AppDatabase.COLUMN_IS_ACTIVE} = 1"
        } else {
            "${AppDatabase.COLUMN_COURSE_ID} = ?"
        }
        
        val cursor = db.query(
            AppDatabase.TABLE_ASSIGNMENTS,
            null,
            selection,
            arrayOf(courseId.toString()),
            null,
            null,
            "${AppDatabase.COLUMN_DEADLINE} ASC"
        )
        
        while (cursor.moveToNext()) {
            assignments.add(cursorToAssignment(cursor))
        }
        
        cursor.close()
        return assignments
    }
    
    fun updateAssignment(assignment: Assignment): Int {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_TITLE, assignment.title)
            put(AppDatabase.COLUMN_DESCRIPTION, assignment.description)
            put(AppDatabase.COLUMN_DEADLINE, assignment.deadline)
            put(AppDatabase.COLUMN_IS_ACTIVE, if (assignment.isActive) 1 else 0)
        }
        
        return db.update(
            AppDatabase.TABLE_ASSIGNMENTS,
            values,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(assignment.id.toString())
        )
    }
    
    fun toggleAssignmentStatus(id: Long, isActive: Boolean): Int {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_IS_ACTIVE, if (isActive) 1 else 0)
        }
        
        return db.update(
            AppDatabase.TABLE_ASSIGNMENTS,
            values,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }
    
    fun deleteAssignment(id: Long): Int {
        val db = database.writableDatabase
        return db.delete(
            AppDatabase.TABLE_ASSIGNMENTS,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }
    
    private fun cursorToAssignment(cursor: Cursor): Assignment {
        val idIndex = cursor.getColumnIndex(AppDatabase.COLUMN_ID)
        val courseIdIndex = cursor.getColumnIndex(AppDatabase.COLUMN_COURSE_ID)
        val titleIndex = cursor.getColumnIndex(AppDatabase.COLUMN_TITLE)
        val descriptionIndex = cursor.getColumnIndex(AppDatabase.COLUMN_DESCRIPTION)
        val deadlineIndex = cursor.getColumnIndex(AppDatabase.COLUMN_DEADLINE)
        val isActiveIndex = cursor.getColumnIndex(AppDatabase.COLUMN_IS_ACTIVE)
        val createdByIndex = cursor.getColumnIndex(AppDatabase.COLUMN_CREATED_BY)
        val createdAtIndex = cursor.getColumnIndex(AppDatabase.COLUMN_CREATED_AT)
        
        return Assignment(
            id = cursor.getLong(idIndex),
            courseId = cursor.getLong(courseIdIndex),
            title = cursor.getString(titleIndex),
            description = if (descriptionIndex != -1 && !cursor.isNull(descriptionIndex)) cursor.getString(descriptionIndex) else null,
            deadline = if (deadlineIndex != -1 && !cursor.isNull(deadlineIndex)) cursor.getString(deadlineIndex) else null,
            isActive = cursor.getInt(isActiveIndex) == 1,
            createdBy = cursor.getLong(createdByIndex),
            createdAt = if (createdAtIndex != -1 && !cursor.isNull(createdAtIndex)) cursor.getString(createdAtIndex) else null
        )
    }
} 