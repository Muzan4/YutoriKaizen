package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.TriggerLog
import kotlinx.coroutines.flow.Flow

interface TriggerLogRepository {
    suspend fun saveLog(timestamp: Long, emotion: String, note: String?): Long
    fun observeAll(): Flow<List<TriggerLog>>
}
