package com.narxoz.myapplication.data

import android.content.ContentValues
import android.database.Cursor
import com.narxoz.myapplication.models.Grade

class GradeDao(private val database: AppDatabase) {

    fun createGrade(grade: Grade): Long {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_ASSIGNMENT_ID, grade.assignmentId)
            put(AppDatabase.COLUMN_STUDENT_ID, grade.studentId)
            put(AppDatabase.COLUMN_SCORE, grade.score)
            put(AppDatabase.COLUMN_COMMENT, grade.comment)
            put(AppDatabase.COLUMN_GRADED_BY, grade.gradedBy)
        }
        
        return db.insert(AppDatabase.TABLE_GRADES, null, values)
    }
    
    fun getGradeById(id: Long): Grade? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_GRADES,
            null,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val grade = cursorToGrade(cursor)
            cursor.close()
            grade
        } else {
            cursor.close()
            null
        }
    }
    
    fun getGradeForAssignmentAndStudent(assignmentId: Long, studentId: Long): Grade? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_GRADES,
            null,
            "${AppDatabase.COLUMN_ASSIGNMENT_ID} = ? AND ${AppDatabase.COLUMN_STUDENT_ID} = ?",
            arrayOf(assignmentId.toString(), studentId.toString()),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val grade = cursorToGrade(cursor)
            cursor.close()
            grade
        } else {
            cursor.close()
            null
        }
    }
    
    fun getGradesForAssignment(assignmentId: Long): List<Grade> {
        val grades = mutableListOf<Grade>()
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_GRADES,
            null,
            "${AppDatabase.COLUMN_ASSIGNMENT_ID} = ?",
            arrayOf(assignmentId.toString()),
            null,
            null,
            null
        )
        
        while (cursor.moveToNext()) {
            grades.add(cursorToGrade(cursor))
        }
        
        cursor.close()
        return grades
    }
    
    fun getGradesForStudent(studentId: Long): List<Grade> {
        val grades = mutableListOf<Grade>()
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_GRADES,
            null,
            "${AppDatabase.COLUMN_STUDENT_ID} = ?",
            arrayOf(studentId.toString()),
            null,
            null,
            null
        )
        
        while (cursor.moveToNext()) {
            grades.add(cursorToGrade(cursor))
        }
        
        cursor.close()
        return grades
    }
    
    fun getGradesForStudentInCourse(studentId: Long, courseId: Long): List<Grade> {
        val grades = mutableListOf<Grade>()
        val db = database.readableDatabase
        val query = """
            SELECT g.* FROM ${AppDatabase.TABLE_GRADES} g
            JOIN ${AppDatabase.TABLE_ASSIGNMENTS} a ON g.${AppDatabase.COLUMN_ASSIGNMENT_ID} = a.${AppDatabase.COLUMN_ID}
            WHERE g.${AppDatabase.COLUMN_STUDENT_ID} = ? AND a.${AppDatabase.COLUMN_COURSE_ID} = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(studentId.toString(), courseId.toString()))
        
        while (cursor.moveToNext()) {
            grades.add(cursorToGrade(cursor))
        }
        
        cursor.close()
        return grades
    }
    
    fun updateGrade(grade: Grade): Int {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_SCORE, grade.score)
            put(AppDatabase.COLUMN_COMMENT, grade.comment)
            put(AppDatabase.COLUMN_GRADED_BY, grade.gradedBy)
        }
        
        return db.update(
            AppDatabase.TABLE_GRADES,
            values,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(grade.id.toString())
        )
    }
    
    fun deleteGrade(id: Long): Int {
        val db = database.writableDatabase
        return db.delete(
            AppDatabase.TABLE_GRADES,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }
    
    private fun cursorToGrade(cursor: Cursor): Grade {
        val idIndex = cursor.getColumnIndex(AppDatabase.COLUMN_ID)
        val assignmentIdIndex = cursor.getColumnIndex(AppDatabase.COLUMN_ASSIGNMENT_ID)
        val studentIdIndex = cursor.getColumnIndex(AppDatabase.COLUMN_STUDENT_ID)
        val scoreIndex = cursor.getColumnIndex(AppDatabase.COLUMN_SCORE)
        val commentIndex = cursor.getColumnIndex(AppDatabase.COLUMN_COMMENT)
        val gradedAtIndex = cursor.getColumnIndex(AppDatabase.COLUMN_GRADED_AT)
        val gradedByIndex = cursor.getColumnIndex(AppDatabase.COLUMN_GRADED_BY)
        
        return Grade(
            id = cursor.getLong(idIndex),
            assignmentId = cursor.getLong(assignmentIdIndex),
            studentId = cursor.getLong(studentIdIndex),
            score = if (scoreIndex != -1 && !cursor.isNull(scoreIndex)) cursor.getFloat(scoreIndex) else null,
            comment = if (commentIndex != -1 && !cursor.isNull(commentIndex)) cursor.getString(commentIndex) else null,
            gradedAt = if (gradedAtIndex != -1 && !cursor.isNull(gradedAtIndex)) cursor.getString(gradedAtIndex) else null,
            gradedBy = cursor.getLong(gradedByIndex)
        )
    }
} 