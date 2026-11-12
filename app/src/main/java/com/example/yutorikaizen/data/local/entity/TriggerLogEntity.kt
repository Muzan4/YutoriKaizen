package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trigger_logs")
data class TriggerLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val emotion: String,       // "BORED" | "TIRED" | "ANXIOUS" | "LONELY" | "AVOIDING"
    val note: String?,
    val actionTaken: String = "YIELDED"  // "YIELDED" | "SURGE_USED" | "REPLACED"
)
