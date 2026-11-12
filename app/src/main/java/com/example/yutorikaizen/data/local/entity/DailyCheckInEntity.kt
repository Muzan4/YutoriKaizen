package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_check_ins")
data class DailyCheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val epochDay: Long,             // LocalDate.toEpochDay() — unique per calendar day
    val phaseNumber: Int,           // 1..4
    val completedHabitIds: String,  // JSON array of completed habit string IDs
    val focusMinutes: Int,          // total focus minutes logged today
    val nciScore: Float             // Neuro-Consistency Index 0f..1f computed at save
)
