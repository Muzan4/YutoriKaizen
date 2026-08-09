package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.TriggerLogDao
import com.example.yutorikaizen.data.local.entity.TriggerLogEntity
import com.example.yutorikaizen.domain.model.TriggerLog
import com.example.yutorikaizen.domain.repository.TriggerLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TriggerLogRepositoryImpl @Inject constructor(
    private val dao: TriggerLogDao
) : TriggerLogRepository {

    override suspend fun saveLog(timestamp: Long, emotion: String, note: String?): Long =
        dao.insert(TriggerLogEntity(timestamp = timestamp, emotion = emotion, note = note))

    override fun observeAll(): Flow<List<TriggerLog>> =
        dao.observeAll().map { list ->
            list.map { e ->
                TriggerLog(id = e.id, timestamp = e.timestamp, emotion = e.emotion, note = e.note)
            }
        }
}
