package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.FocusSessionDao
import com.example.yutorikaizen.data.local.entity.FocusSessionEntity
import com.example.yutorikaizen.domain.model.FocusSession
import com.example.yutorikaizen.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Bridges the domain interface with the Room DAO.
 * Maps between FocusSessionEntity (Room) ↔ FocusSession (domain) so upper layers
 * never import any Room class.
 */
class FocusSessionRepositoryImpl @Inject constructor(
    private val dao: FocusSessionDao
) : FocusSessionRepository {

    override suspend fun saveSession(
        startTime: Long,
        durationMinutes: Int,
        completed: Boolean
    ): Long = dao.insert(
        FocusSessionEntity(
            startTime       = startTime,
            durationMinutes = durationMinutes,
            completed       = completed
        )
    )

    override fun observeAllSessions(): Flow<List<FocusSession>> =
        dao.observeAll().map { list ->
            list.map { e ->
                FocusSession(
                    id              = e.id,
                    startTime       = e.startTime,
                    durationMinutes = e.durationMinutes,
                    completed       = e.completed
                )
            }
        }

    override suspend fun countCompletedForDate(date: String): Int =
        dao.countCompletedForDate(date)
}
