package com.example.yutorikaizen.domain.usecase.focus

import com.example.yutorikaizen.domain.model.DailyCheckIn
import com.example.yutorikaizen.domain.repository.DailyCheckInRepository
import com.example.yutorikaizen.domain.repository.FocusSessionRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Saves a completed (or abandoned) focus session to the database.
 * If completed, also updates today's daily check-in focus minutes.
 */
class CompleteFocusSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository,
    private val dailyCheckInRepository: DailyCheckInRepository
) {
    suspend operator fun invoke(startTime: Long, durationMinutes: Int, completed: Boolean) {
        repository.saveSession(
            startTime       = startTime,
            durationMinutes = durationMinutes,
            completed       = completed
        )
        if (completed) {
            val existing = dailyCheckInRepository.getForToday()
            val newFocusMinutes = (existing?.focusMinutes ?: 0) + durationMinutes
            val todayCheckIn = existing?.copy(focusMinutes = newFocusMinutes)
                ?: DailyCheckIn(
                    id = 0,
                    epochDay = LocalDate.now().toEpochDay(),
                    phaseNumber = 1,
                    completedHabitIds = emptyList(),
                    focusMinutes = newFocusMinutes,
                    nciScore = 0f
                )
            dailyCheckInRepository.upsert(todayCheckIn)
        }
    }
}
