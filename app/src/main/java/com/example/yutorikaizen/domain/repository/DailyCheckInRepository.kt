package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.DailyCheckIn
import kotlinx.coroutines.flow.Flow

interface DailyCheckInRepository {
    suspend fun upsert(checkIn: DailyCheckIn)
    suspend fun getForToday(): DailyCheckIn?
    fun observeLast14Days(): Flow<List<DailyCheckIn>>
    suspend fun getAll(): List<DailyCheckIn>
    suspend fun deleteAll()
}
