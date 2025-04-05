package com.narxoz.myapplication.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.narxoz.myapplication.models.Course
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.models.UserRole

class CourseDao(private val database: AppDatabase) {

    fun createCourse(course: Course): Long {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_TITLE, course.title)
            put(AppDatabase.COLUMN_DESCRIPTION, course.description)
            put(AppDatabase.COLUMN_START_DATE, course.startDate)
            put(AppDatabase.COLUMN_END_DATE, course.endDate)
            put(AppDatabase.COLUMN_IS_ACTIVE, if (course.isActive) 1 else 0)
            put(AppDatabase.COLUMN_CREATED_BY, course.createdBy)
        }
        
        return db.insert(AppDatabase.TABLE_COURSES, null, values)
    }
    
    fun getCourseById(id: Long): Course? {
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_COURSES,
            null,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        
        return if (cursor.moveToFirst()) {
            val course = cursorToCourse(cursor)
            cursor.close()
            course
        } else {
            cursor.close()
            null
        }
    }
    
    fun getAllCourses(): List<Course> {
        val courses = mutableListOf<Course>()
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_COURSES,
            null,
            null,
            null,
            null,
            null,
            null
        )
        
        while (cursor.moveToNext()) {
            courses.add(cursorToCourse(cursor))
        }
        
        cursor.close()
        return courses
    }
    
    fun getActiveCourses(): List<Course> {
        val courses = mutableListOf<Course>()
        val db = database.readableDatabase
        val cursor = db.query(
            AppDatabase.TABLE_COURSES,
            null,
            "${AppDatabase.COLUMN_IS_ACTIVE} = ?",
            arrayOf("1"),
            null,
            null,
            null
        )
        
        while (cursor.moveToNext()) {
            courses.add(cursorToCourse(cursor))
        }
        
        cursor.close()
        return courses
    }
    
    fun updateCourse(course: Course): Int {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_TITLE, course.title)
            put(AppDatabase.COLUMN_DESCRIPTION, course.description)
            put(AppDatabase.COLUMN_START_DATE, course.startDate)
            put(AppDatabase.COLUMN_END_DATE, course.endDate)
            put(AppDatabase.COLUMN_IS_ACTIVE, if (course.isActive) 1 else 0)
        }
        
        return db.update(
            AppDatabase.TABLE_COURSES,
            values,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(course.id.toString())
        )
    }
    
    fun deleteCourse(id: Long): Int {
        val db = database.writableDatabase
        return db.delete(
            AppDatabase.TABLE_COURSES,
            "${AppDatabase.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    
    fun assignTeacherToCourse(courseId: Long, teacherId: Long): Long {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_COURSE_ID, courseId)
            put(AppDatabase.COLUMN_TEACHER_ID, teacherId)
        }
        
        return db.insert(AppDatabase.TABLE_COURSE_TEACHERS, null, values)
    }
    
    fun removeTeacherFromCourse(courseId: Long, teacherId: Long): Int {
        val db = database.writableDatabase
        return db.delete(
            AppDatabase.TABLE_COURSE_TEACHERS,
            "${AppDatabase.COLUMN_COURSE_ID} = ? AND ${AppDatabase.COLUMN_TEACHER_ID} = ?",
            arrayOf(courseId.toString(), teacherId.toString())
        )
    }
    
    fun enrollStudentInCourse(courseId: Long, studentId: Long): Long {
        val db = database.writableDatabase
        val values = ContentValues().apply {
            put(AppDatabase.COLUMN_COURSE_ID, courseId)
            put(AppDatabase.COLUMN_STUDENT_ID, studentId)
        }
        
        return db.insert(AppDatabase.TABLE_COURSE_STUDENTS, null, values)
    }
    
    fun removeStudentFromCourse(courseId: Long, studentId: Long): Int {
        val db = database.writableDatabase
        return db.delete(
            AppDatabase.TABLE_COURSE_STUDENTS,
            "${AppDatabase.COLUMN_COURSE_ID} = ? AND ${AppDatabase.COLUMN_STUDENT_ID} = ?",
            arrayOf(courseId.toString(), studentId.toString())
        )
    }
    
    @SuppressLint("Range")
    fun getTeachersForCourse(courseId: Long): List<User> {
        val teachers = mutableListOf<User>()
        val db = database.readableDatabase
        val query = """
            SELECT u.* FROM ${AppDatabase.TABLE_USERS} u
            JOIN ${AppDatabase.TABLE_COURSE_TEACHERS} ct ON u.${AppDatabase.COLUMN_ID} = ct.${AppDatabase.COLUMN_TEACHER_ID}
            WHERE ct.${AppDatabase.COLUMN_COURSE_ID} = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(courseId.toString()))
        val userDao = UserDao(database)
        
        while (cursor.moveToNext()) {
            val userId = cursor.getLong(cursor.getColumnIndex(AppDatabase.COLUMN_ID))
            userDao.getUserById(userId)?.let { teachers.add(it) }
        }
        
        cursor.close()
        return teachers
    }
    
    @SuppressLint("Range")
    fun getStudentsForCourse(courseId: Long): List<User> {
        val students = mutableListOf<User>()
        val db = database.readableDatabase
        val query = """
            SELECT u.* FROM ${AppDatabase.TABLE_USERS} u
            JOIN ${AppDatabase.TABLE_COURSE_STUDENTS} cs ON u.${AppDatabase.COLUMN_ID} = cs.${AppDatabase.COLUMN_STUDENT_ID}
            WHERE cs.${AppDatabase.COLUMN_COURSE_ID} = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(courseId.toString()))
        val userDao = UserDao(database)
        
        while (cursor.moveToNext()) {
            val userId = cursor.getLong(cursor.getColumnIndex(AppDatabase.COLUMN_ID))
            userDao.getUserById(userId)?.let { students.add(it) }
        }
        
        cursor.close()
        return students
    }
    
    fun getCoursesForTeacher(teacherId: Long): List<Course> {
        val courses = mutableListOf<Course>()
        val db = database.readableDatabase
        val query = """
            SELECT c.* FROM ${AppDatabase.TABLE_COURSES} c
            JOIN ${AppDatabase.TABLE_COURSE_TEACHERS} ct ON c.${AppDatabase.COLUMN_ID} = ct.${AppDatabase.COLUMN_COURSE_ID}
            WHERE ct.${AppDatabase.COLUMN_TEACHER_ID} = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(teacherId.toString()))
        
        while (cursor.moveToNext()) {
            courses.add(cursorToCourse(cursor))
        }
        
        cursor.close()
        return courses
    }
    
    fun getCoursesForStudent(studentId: Long): List<Course> {
        val courses = mutableListOf<Course>()
        val db = database.readableDatabase
        val query = """
            SELECT c.* FROM ${AppDatabase.TABLE_COURSES} c
            JOIN ${AppDatabase.TABLE_COURSE_STUDENTS} cs ON c.${AppDatabase.COLUMN_ID} = cs.${AppDatabase.COLUMN_COURSE_ID}
            WHERE cs.${AppDatabase.COLUMN_STUDENT_ID} = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(studentId.toString()))
        
        while (cursor.moveToNext()) {
            courses.add(cursorToCourse(cursor))
        }
        
        cursor.close()
        return courses
    }
    
    private fun cursorToCourse(cursor: Cursor): Course {
        val idIndex = cursor.getColumnIndex(AppDatabase.COLUMN_ID)
        val titleIndex = cursor.getColumnIndex(AppDatabase.COLUMN_TITLE)
        val descriptionIndex = cursor.getColumnIndex(AppDatabase.COLUMN_DESCRIPTION)
        val startDateIndex = cursor.getColumnIndex(AppDatabase.COLUMN_START_DATE)
        val endDateIndex = cursor.getColumnIndex(AppDatabase.COLUMN_END_DATE)
        val isActiveIndex = cursor.getColumnIndex(AppDatabase.COLUMN_IS_ACTIVE)
        val createdByIndex = cursor.getColumnIndex(AppDatabase.COLUMN_CREATED_BY)
        val createdAtIndex = cursor.getColumnIndex(AppDatabase.COLUMN_CREATED_AT)
        
        return Course(
            id = cursor.getLong(idIndex),
            title = cursor.getString(titleIndex),
            description = if (descriptionIndex != -1 && !cursor.isNull(descriptionIndex)) cursor.getString(descriptionIndex) else null,
            startDate = if (startDateIndex != -1 && !cursor.isNull(startDateIndex)) cursor.getString(startDateIndex) else null,
            endDate = if (endDateIndex != -1 && !cursor.isNull(endDateIndex)) cursor.getString(endDateIndex) else null,
            isActive = cursor.getInt(isActiveIndex) == 1,
            createdBy = if (createdByIndex != -1 && !cursor.isNull(createdByIndex)) cursor.getLong(createdByIndex) else null,
            createdAt = if (createdAtIndex != -1 && !cursor.isNull(createdAtIndex)) cursor.getString(createdAtIndex) else null
        )
    }
} 