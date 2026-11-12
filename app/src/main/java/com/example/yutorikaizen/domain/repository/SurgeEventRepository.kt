package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.SurgeEvent
import kotlinx.coroutines.flow.Flow

interface SurgeEventRepository {
    suspend fun saveEvent(event: SurgeEvent)
    fun observeRecent(): Flow<List<SurgeEvent>>
    suspend fun getEventsSince(sinceMs: Long): List<SurgeEvent>
    suspend fun countResisted(): Int
    suspend fun countYielded(): Int
    suspend fun deleteAll()
}
