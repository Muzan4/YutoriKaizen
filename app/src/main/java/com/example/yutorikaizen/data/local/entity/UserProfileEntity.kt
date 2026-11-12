package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 0,   // singleton row
    val startDate: Long,           // epoch millis when journey began
    val difficultyLevel: String,   // "GENTLE" | "CHALLENGER" | "HARD"
    val selectedTriggers: String,  // JSON array e.g. ["BOREDOM","ANXIETY"]
    val selectedProgramId: String, // id from DopamineProgram catalog
    val onboardingComplete: Boolean = false
)
