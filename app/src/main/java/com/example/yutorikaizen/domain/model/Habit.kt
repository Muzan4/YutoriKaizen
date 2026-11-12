package com.example.yutorikaizen.domain.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String,
    val emoji: String,
    val colorHex: String = "#7CB47A",
    val isGoodHabit: Boolean = true,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val targetCount: Int = 1,
    val unit: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)

enum class HabitFrequency(val label: String) {
    DAILY("Every Day"),
    WEEKDAYS("Weekdays"),
    WEEKENDS("Weekends")
}

data class HabitLog(
    val id: Long = 0,
    val habitId: Long,
    val epochDay: Long,
    val completedCount: Int = 0,
    val note: String = "",
    val loggedAt: Long = System.currentTimeMillis()
)

data class HabitWithLog(
    val habit: Habit,
    val todayLog: HabitLog?,
    val streak: Int = 0,
    val totalDays: Int = 0,
    val last7Days: List<Boolean> = emptyList() // true = completed that day
)
