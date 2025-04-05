package com.narxoz.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.narxoz.myapplication.models.UserRole
import com.narxoz.myapplication.ui.admin.CourseManagementFragment
import com.narxoz.myapplication.ui.admin.EnrollmentManagementFragment
import com.narxoz.myapplication.ui.admin.UserManagementFragment
import com.narxoz.myapplication.ui.login.LoginActivity
import com.narxoz.myapplication.ui.student.StudentAssignmentsFragment
import com.narxoz.myapplication.ui.student.StudentCoursesFragment
import com.narxoz.myapplication.ui.student.StudentGradesFragment
import com.narxoz.myapplication.ui.teacher.TeacherAssignmentsFragment
import com.narxoz.myapplication.ui.teacher.TeacherCoursesFragment
import com.narxoz.myapplication.ui.teacher.TeacherGradingFragment
import com.narxoz.myapplication.utils.SessionManager

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        sessionManager = SessionManager.getInstance(this)
        
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)
        
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        
        val headerView = navigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nav_user_name)
        val roleTextView = headerView.findViewById<TextView>(R.id.nav_user_role)
        
        nameTextView.text = sessionManager.getFullName()
        roleTextView.text = sessionManager.getUserRole()?.name ?: "Guest"
        
        showMenuItemsByUserRole(sessionManager.getUserRole())
        
        if (savedInstanceState == null) {
            showDefaultFragmentByUserRole(sessionManager.getUserRole())
        }
    }
    
    private fun showMenuItemsByUserRole(userRole: UserRole?) {
        val adminGroup = navigationView.menu.findItem(R.id.admin_group)?.subMenu
        val teacherGroup = navigationView.menu.findItem(R.id.teacher_group)?.subMenu
        val studentGroup = navigationView.menu.findItem(R.id.student_group)?.subMenu
        
        when (userRole) {
            UserRole.ADMIN -> {
                navigationView.menu.setGroupVisible(R.id.admin_group, true)
                navigationView.menu.setGroupVisible(R.id.teacher_group, false)
                navigationView.menu.setGroupVisible(R.id.student_group, false)
            }
            UserRole.TEACHER -> {
                navigationView.menu.setGroupVisible(R.id.admin_group, false)
                navigationView.menu.setGroupVisible(R.id.teacher_group, true)
                navigationView.menu.setGroupVisible(R.id.student_group, false)
            }
            UserRole.STUDENT -> {
                navigationView.menu.setGroupVisible(R.id.admin_group, false)
                navigationView.menu.setGroupVisible(R.id.teacher_group, false)
                navigationView.menu.setGroupVisible(R.id.student_group, true)
            }
            else -> {
                navigationView.menu.setGroupVisible(R.id.admin_group, false)
                navigationView.menu.setGroupVisible(R.id.teacher_group, false)
                navigationView.menu.setGroupVisible(R.id.student_group, false)
            }
        }
    }
    
    private fun showDefaultFragmentByUserRole(userRole: UserRole?) {
        when (userRole) {
            UserRole.ADMIN -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    CourseManagementFragment()
                ).commit()
                navigationView.setCheckedItem(R.id.nav_courses)
            }
            UserRole.TEACHER -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    TeacherCoursesFragment()
                ).commit()
                navigationView.setCheckedItem(R.id.nav_my_courses)
            }
            UserRole.STUDENT -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    StudentCoursesFragment()
                ).commit()
                navigationView.setCheckedItem(R.id.nav_enrolled_courses)
            }
            else -> {
                finish()
            }
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_courses -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    CourseManagementFragment()
                ).commit()
            }
            R.id.nav_users -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    UserManagementFragment()
                ).commit()
            }
            R.id.nav_enrollments -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    EnrollmentManagementFragment()
                ).commit()
            }
            
            R.id.nav_my_courses -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    TeacherCoursesFragment()
                ).commit()
            }
            R.id.nav_assignments -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    TeacherAssignmentsFragment()
                ).commit()
            }
            R.id.nav_grading -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    TeacherGradingFragment()
                ).commit()
            }
            
            R.id.nav_enrolled_courses -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    StudentCoursesFragment()
                ).commit()
            }
            R.id.nav_my_assignments -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    StudentAssignmentsFragment()
                ).commit()
            }
            R.id.nav_my_grades -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container,
                    StudentGradesFragment()
                ).commit()
            }
            
            R.id.nav_profile -> {
            }
            R.id.nav_logout -> {
                sessionManager.logoutUser()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}