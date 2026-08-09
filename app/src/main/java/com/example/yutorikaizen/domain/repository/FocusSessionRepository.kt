package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.FocusSession
import kotlinx.coroutines.flow.Flow

/**
 * Contract for focus session persistence.
 * WHY an interface here? The domain layer must not depend on Room (a data-layer detail).
 * This interface is the boundary — the domain defines what it needs, the data layer provides it.
 */
interface FocusSessionRepository {
    suspend fun saveSession(startTime: Long, durationMinutes: Int, completed: Boolean): Long
    fun observeAllSessions(): Flow<List<FocusSession>>
    suspend fun countCompletedForDate(date: String): Int
}
