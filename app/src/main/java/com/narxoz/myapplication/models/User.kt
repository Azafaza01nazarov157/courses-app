package com.narxoz.myapplication.models

data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val role: UserRole,
    val fullName: String,
    val email: String? = null,
    val createdAt: String? = null
)

enum class UserRole {
    ADMIN, TEACHER, STUDENT;
    
    companion object {
        fun fromString(role: String): UserRole {
            return when (role.lowercase()) {
                "admin" -> ADMIN
                "teacher" -> TEACHER
                "student" -> STUDENT
                else -> throw IllegalArgumentException("Unknown role: $role")
            }
        }
    }
} 