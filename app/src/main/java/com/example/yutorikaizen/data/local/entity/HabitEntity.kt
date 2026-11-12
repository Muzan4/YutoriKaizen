package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val emoji: String,
    val colorHex: String, // e.g. "#7CB47A"
    val isGoodHabit: Boolean = true, // true = trying to build, false = trying to break
    val frequency: String = "DAILY", // DAILY, WEEKDAYS, WEEKENDS
    val targetCount: Int = 1, // per day target (e.g. drink 8 glasses)
    val unit: String = "", // e.g. "glasses", "minutes", "times"
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val sortOrder: Int = 0
)
