package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(): Flow<UserProfile?>
    suspend fun getProfile(): UserProfile?
    suspend fun saveProfile(profile: UserProfile)
    suspend fun resetJourney(startDateMs: Long = System.currentTimeMillis())
}
