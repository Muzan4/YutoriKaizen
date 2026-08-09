package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,        // epoch millis — when the session started
    val durationMinutes: Int,   // planned duration (e.g. 25)
    val completed: Boolean      // true = finished naturally; false = abandoned via Reset
)
