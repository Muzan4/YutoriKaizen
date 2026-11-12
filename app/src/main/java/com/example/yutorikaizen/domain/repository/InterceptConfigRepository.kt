package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.data.local.entity.InterceptConfigEntity
import kotlinx.coroutines.flow.Flow

interface InterceptConfigRepository {
    fun observeAll(): Flow<List<InterceptConfigEntity>>
    suspend fun upsert(config: InterceptConfigEntity)
    suspend fun delete(config: InterceptConfigEntity)
    suspend fun setEnabled(packageName: String, enabled: Boolean)
}
