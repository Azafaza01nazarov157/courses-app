package com.narxoz.myapplication.models

data class Assignment(
    val id: Long = 0,
    val courseId: Long,
    val title: String,
    val description: String? = null,
    val deadline: String? = null,
    val isActive: Boolean = true,
    val createdBy: Long,
    val createdAt: String? = null
) 