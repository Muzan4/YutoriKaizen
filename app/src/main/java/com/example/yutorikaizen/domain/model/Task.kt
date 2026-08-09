package com.example.yutorikaizen.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val date: String,          // "YYYY-MM-DD"
    val completed: Boolean = false
)
