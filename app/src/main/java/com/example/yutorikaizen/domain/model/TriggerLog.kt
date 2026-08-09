package com.example.yutorikaizen.domain.model

data class TriggerLog(
    val id: Long = 0,
    val timestamp: Long,
    val emotion: String,
    val note: String?
)
