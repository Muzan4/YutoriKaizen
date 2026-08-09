package com.example.yutorikaizen.domain.model

/** Pure Kotlin domain model — no Room/Android imports. */
data class FocusSession(
    val id: Long = 0,
    val startTime: Long,       // epoch millis
    val durationMinutes: Int,
    val completed: Boolean
)
