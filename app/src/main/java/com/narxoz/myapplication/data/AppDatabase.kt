package com.narxoz.myapplication.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class AppDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "CourseManagement.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val TABLE_COURSES = "courses"
        const val TABLE_COURSE_TEACHERS = "course_teachers"
        const val TABLE_COURSE_STUDENTS = "course_students"
        const val TABLE_ASSIGNMENTS = "assignments"
        const val TABLE_GRADES = "grades"

        const val COLUMN_ID = "id"
        const val COLUMN_CREATED_AT = "created_at"

        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROLE = "role"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_EMAIL = "email"

        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_START_DATE = "start_date"
        const val COLUMN_END_DATE = "end_date"
        const val COLUMN_IS_ACTIVE = "is_active"
        const val COLUMN_CREATED_BY = "created_by"

        const val COLUMN_COURSE_ID = "course_id"
        const val COLUMN_TEACHER_ID = "teacher_id"

        const val COLUMN_STUDENT_ID = "student_id"
        const val COLUMN_ENROLLMENT_DATE = "enrollment_date"

        const val COLUMN_DEADLINE = "deadline"

        const val COLUMN_ASSIGNMENT_ID = "assignment_id"
        const val COLUMN_SCORE = "score"
        const val COLUMN_COMMENT = "comment"
        const val COLUMN_GRADED_AT = "graded_at"
        const val COLUMN_GRADED_BY = "graded_by"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_ROLE TEXT NOT NULL,
                $COLUMN_FULL_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()

        val createCoursesTable = """
            CREATE TABLE $TABLE_COURSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_START_DATE TEXT,
                $COLUMN_END_DATE TEXT,
                $COLUMN_IS_ACTIVE INTEGER DEFAULT 1,
                $COLUMN_CREATED_BY INTEGER,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_CREATED_BY) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()

        val createCourseTeachersTable = """
            CREATE TABLE $TABLE_COURSE_TEACHERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COURSE_ID INTEGER NOT NULL,
                $COLUMN_TEACHER_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_COURSE_ID) REFERENCES $TABLE_COURSES($COLUMN_ID),
                FOREIGN KEY ($COLUMN_TEACHER_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
                UNIQUE($COLUMN_COURSE_ID, $COLUMN_TEACHER_ID)
            )
        """.trimIndent()

        val createCourseStudentsTable = """
            CREATE TABLE $TABLE_COURSE_STUDENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COURSE_ID INTEGER NOT NULL,
                $COLUMN_STUDENT_ID INTEGER NOT NULL,
                $COLUMN_ENROLLMENT_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_COURSE_ID) REFERENCES $TABLE_COURSES($COLUMN_ID),
                FOREIGN KEY ($COLUMN_STUDENT_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
                UNIQUE($COLUMN_COURSE_ID, $COLUMN_STUDENT_ID)
            )
        """.trimIndent()

        val createAssignmentsTable = """
            CREATE TABLE $TABLE_ASSIGNMENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COURSE_ID INTEGER NOT NULL,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DEADLINE TEXT,
                $COLUMN_IS_ACTIVE INTEGER DEFAULT 1,
                $COLUMN_CREATED_BY INTEGER NOT NULL,
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_COURSE_ID) REFERENCES $TABLE_COURSES($COLUMN_ID),
                FOREIGN KEY ($COLUMN_CREATED_BY) REFERENCES $TABLE_USERS($COLUMN_ID)
            )
        """.trimIndent()

        val createGradesTable = """
            CREATE TABLE $TABLE_GRADES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ASSIGNMENT_ID INTEGER NOT NULL,
                $COLUMN_STUDENT_ID INTEGER NOT NULL,
                $COLUMN_SCORE REAL,
                $COLUMN_COMMENT TEXT,
                $COLUMN_GRADED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_GRADED_BY INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_ASSIGNMENT_ID) REFERENCES $TABLE_ASSIGNMENTS($COLUMN_ID),
                FOREIGN KEY ($COLUMN_STUDENT_ID) REFERENCES $TABLE_USERS($COLUMN_ID),
                FOREIGN KEY ($COLUMN_GRADED_BY) REFERENCES $TABLE_USERS($COLUMN_ID),
                UNIQUE($COLUMN_ASSIGNMENT_ID, $COLUMN_STUDENT_ID)
            )
        """.trimIndent()

        try {
            db.execSQL(createUsersTable)
            db.execSQL(createCoursesTable)
            db.execSQL(createCourseTeachersTable)
            db.execSQL(createCourseStudentsTable)
            db.execSQL(createAssignmentsTable)
            db.execSQL(createGradesTable)
            
            db.execSQL("""
                INSERT INTO $TABLE_USERS 
                ($COLUMN_USERNAME, $COLUMN_PASSWORD, $COLUMN_ROLE, $COLUMN_FULL_NAME, $COLUMN_EMAIL)
                VALUES ('admin', 'admin123', 'admin', 'Administrator', 'admin@example.com')
            """.trimIndent())
            
        } catch (e: Exception) {
            Log.e("AppDatabase", "Error creating database: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GRADES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ASSIGNMENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COURSE_STUDENTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COURSE_TEACHERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COURSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
} 