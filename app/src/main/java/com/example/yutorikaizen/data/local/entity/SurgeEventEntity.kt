package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surge_events")
data class SurgeEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val durationSeconds: Int,      // how long the delay lasted
    val outcome: String,           // "RESISTED" | "YIELDED" | "INCOMPLETE"
    val methodUsed: String,        // "BREATHING" | "CBT" | "PHYSICAL" | ""
    val emotionLogged: String?     // optional emotion captured during surge
)
