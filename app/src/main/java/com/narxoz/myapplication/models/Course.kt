package com.narxoz.myapplication.models

data class Course(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val isActive: Boolean = true,
    val createdBy: Long? = null,
    val createdAt: String? = null
) 