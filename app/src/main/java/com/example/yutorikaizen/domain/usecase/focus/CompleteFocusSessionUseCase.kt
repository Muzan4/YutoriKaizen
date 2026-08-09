package com.example.yutorikaizen.domain.usecase.focus

import com.example.yutorikaizen.domain.repository.FocusSessionRepository
import javax.inject.Inject

/**
 * Saves a completed (or abandoned) focus session to the database.
 *
 * WHY a use case instead of calling the repository directly from the ViewModel?
 * The ViewModel is a UI controller — it should not contain business logic.
 * Use cases are single-purpose: one class, one job. This makes them easy to test
 * and easy to reuse from multiple places (e.g. a future widget or notification action).
 */
class CompleteFocusSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(startTime: Long, durationMinutes: Int, completed: Boolean) {
        repository.saveSession(
            startTime       = startTime,
            durationMinutes = durationMinutes,
            completed       = completed
        )
    }
}
