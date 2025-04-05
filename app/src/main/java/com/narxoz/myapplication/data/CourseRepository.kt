package com.narxoz.myapplication.data

import android.content.Context
import com.narxoz.myapplication.models.Assignment
import com.narxoz.myapplication.models.Course
import com.narxoz.myapplication.models.Grade
import com.narxoz.myapplication.models.User
import com.narxoz.myapplication.models.UserRole

class CourseRepository private constructor(context: Context) {
    
    private val database: AppDatabase = AppDatabase(context)
    private val userDao: UserDao = UserDao(database)
    private val courseDao: CourseDao = CourseDao(database)
    private val assignmentDao: AssignmentDao = AssignmentDao(database)
    private val gradeDao: GradeDao = GradeDao(database)
    
    companion object {
        @Volatile
        private var instance: CourseRepository? = null
        
        fun getInstance(context: Context): CourseRepository {
            return instance ?: synchronized(this) {
                instance ?: CourseRepository(context.applicationContext).also { instance = it }
            }
        }
    }
    
    fun authenticateUser(username: String, password: String): User? {
        return userDao.authenticateUser(username, password)
    }
    
    fun createUser(user: User): Long {
        return userDao.createUser(user)
    }
    
    fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }
    
    fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }
    
    fun getUsersByRole(role: UserRole): List<User> {
        return userDao.getUsersByRole(role)
    }
    
    fun updateUser(user: User): Int {
        return userDao.updateUser(user)
    }
    
    fun deleteUser(id: Long): Int {
        return userDao.deleteUser(id)
    }
    
    fun createCourse(course: Course): Long {
        return courseDao.createCourse(course)
    }
    
    fun getCourseById(id: Long): Course? {
        return courseDao.getCourseById(id)
    }
    
    fun getAllCourses(): List<Course> {
        return courseDao.getAllCourses()
    }
    
    fun getActiveCourses(): List<Course> {
        return courseDao.getActiveCourses()
    }
    
    fun updateCourse(course: Course): Int {
        return courseDao.updateCourse(course)
    }
    
    fun deleteCourse(id: Long): Int {
        return courseDao.deleteCourse(id)
    }
    
    fun assignTeacherToCourse(courseId: Long, teacherId: Long): Long {
        return courseDao.assignTeacherToCourse(courseId, teacherId)
    }
    
    fun removeTeacherFromCourse(courseId: Long, teacherId: Long): Int {
        return courseDao.removeTeacherFromCourse(courseId, teacherId)
    }
    
    fun getTeachersForCourse(courseId: Long): List<User> {
        return courseDao.getTeachersForCourse(courseId)
    }
    
    fun getCoursesForTeacher(teacherId: Long): List<Course> {
        return courseDao.getCoursesForTeacher(teacherId)
    }
    
    fun enrollStudentInCourse(courseId: Long, studentId: Long): Long {
        return courseDao.enrollStudentInCourse(courseId, studentId)
    }
    
    fun removeStudentFromCourse(courseId: Long, studentId: Long): Int {
        return courseDao.removeStudentFromCourse(courseId, studentId)
    }
    
    fun getStudentsForCourse(courseId: Long): List<User> {
        return courseDao.getStudentsForCourse(courseId)
    }
    
    fun getCoursesForStudent(studentId: Long): List<Course> {
        return courseDao.getCoursesForStudent(studentId)
    }
    
    fun createAssignment(assignment: Assignment): Long {
        return assignmentDao.createAssignment(assignment)
    }
    
    fun getAssignmentById(id: Long): Assignment? {
        return assignmentDao.getAssignmentById(id)
    }
    
    fun getAssignmentsForCourse(courseId: Long, activeOnly: Boolean = false): List<Assignment> {
        return assignmentDao.getAssignmentsForCourse(courseId, activeOnly)
    }
    
    fun updateAssignment(assignment: Assignment): Int {
        return assignmentDao.updateAssignment(assignment)
    }
    
    fun toggleAssignmentStatus(id: Long, isActive: Boolean): Int {
        return assignmentDao.toggleAssignmentStatus(id, isActive)
    }
    
    fun deleteAssignment(id: Long): Int {
        return assignmentDao.deleteAssignment(id)
    }
    
    fun createGrade(grade: Grade): Long {
        return gradeDao.createGrade(grade)
    }
    
    fun getGradeById(id: Long): Grade? {
        return gradeDao.getGradeById(id)
    }
    
    fun getGradeForAssignmentAndStudent(assignmentId: Long, studentId: Long): Grade? {
        return gradeDao.getGradeForAssignmentAndStudent(assignmentId, studentId)
    }
    
    fun getGradesForAssignment(assignmentId: Long): List<Grade> {
        return gradeDao.getGradesForAssignment(assignmentId)
    }
    
    fun getGradesForStudent(studentId: Long): List<Grade> {
        return gradeDao.getGradesForStudent(studentId)
    }
    
    fun getGradesForStudentInCourse(studentId: Long, courseId: Long): List<Grade> {
        return gradeDao.getGradesForStudentInCourse(studentId, courseId)
    }
    
    fun updateGrade(grade: Grade): Int {
        return gradeDao.updateGrade(grade)
    }
    
    fun deleteGrade(id: Long): Int {
        return gradeDao.deleteGrade(id)
    }
} 