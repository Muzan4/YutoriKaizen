package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_usage_events",
    indices = [Index(value = ["packageName", "epochDay"], unique = true)]
)
data class AppUsageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val appLabel: String,
    val epochDay: Long,
    val totalForegroundMs: Long,
    val launchCount: Int
)
