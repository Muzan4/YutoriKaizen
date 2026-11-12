package com.example.yutorikaizen.domain.model

data class DailyCheckIn(
    val id: Long = 0,
    val epochDay: Long,
    val phaseNumber: Int,
    val completedHabitIds: List<String>,
    val focusMinutes: Int,
    val nciScore: Float
)
