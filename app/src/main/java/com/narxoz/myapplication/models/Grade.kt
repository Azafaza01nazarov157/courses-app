package com.narxoz.myapplication.models

data class Grade(
    val id: Long = 0,
    val assignmentId: Long,
    val studentId: Long,
    val score: Float? = null,
    val comment: String? = null,
    val gradedAt: String? = null,
    val gradedBy: Long
) 