package com.example.yutorikaizen.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    indices = [
        Index(value = ["habitId", "epochDay"], unique = true)
    ]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val epochDay: Long, // LocalDate.now().toEpochDay()
    @ColumnInfo(defaultValue = "0") val completedCount: Int = 0, // how many times completed today
    @ColumnInfo(defaultValue = "") val note: String = "",
    val loggedAt: Long = System.currentTimeMillis()
)
