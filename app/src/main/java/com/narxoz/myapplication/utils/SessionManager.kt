package com.narxoz.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import com.narxoz.myapplication.models.UserRole

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    
    companion object {
        private const val PREF_NAME = "CourseManagementSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USERNAME = "username"
        private const val KEY_FULL_NAME = "fullName"
        private const val KEY_ROLE = "role"
        
        @Volatile
        private var instance: SessionManager? = null
        
        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    fun createSession(userId: Long, username: String, fullName: String, role: UserRole) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_USER_ID, userId)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_FULL_NAME, fullName)
        editor.putString(KEY_ROLE, role.name)
        editor.apply()
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1)
    }
    
    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }
    
    fun getFullName(): String? {
        return sharedPreferences.getString(KEY_FULL_NAME, null)
    }
    
    fun getUserRole(): UserRole? {
        val roleName = sharedPreferences.getString(KEY_ROLE, null)
        return roleName?.let { UserRole.valueOf(it) }
    }
    
    fun logoutUser() {
        editor.clear()
        editor.apply()
    }
} 