package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.SurgeEventDao
import com.example.yutorikaizen.data.local.entity.SurgeEventEntity
import com.example.yutorikaizen.domain.model.SurgeEvent
import com.example.yutorikaizen.domain.model.SurgeMethod
import com.example.yutorikaizen.domain.model.SurgeOutcome
import com.example.yutorikaizen.domain.repository.SurgeEventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SurgeEventRepositoryImpl @Inject constructor(
    private val dao: SurgeEventDao
) : SurgeEventRepository {

    override suspend fun saveEvent(event: SurgeEvent) {
        dao.insert(event.toEntity())
    }

    override fun observeRecent(): Flow<List<SurgeEvent>> =
        dao.observeRecent().map { list -> list.map { it.toDomain() } }

    override suspend fun getEventsSince(sinceMs: Long): List<SurgeEvent> =
        dao.getSince(sinceMs).map { it.toDomain() }

    override suspend fun countResisted(): Int = dao.countResisted()
    override suspend fun countYielded(): Int  = dao.countYielded()

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun SurgeEventEntity.toDomain() = SurgeEvent(
        id              = id,
        timestamp       = timestamp,
        durationSeconds = durationSeconds,
        outcome         = SurgeOutcome.valueOf(outcome),
        methodUsed      = SurgeMethod.valueOf(methodUsed),
        emotionLogged   = emotionLogged
    )

    private fun SurgeEvent.toEntity() = SurgeEventEntity(
        id              = id,
        timestamp       = timestamp,
        durationSeconds = durationSeconds,
        outcome         = outcome.name,
        methodUsed      = methodUsed.name,
        emotionLogged   = emotionLogged
    )
}
