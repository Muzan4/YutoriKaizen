package com.example.yutorikaizen.domain.usecase

import com.example.yutorikaizen.domain.model.DifficultyLevel
import com.example.yutorikaizen.domain.model.NeuralPhase
import com.example.yutorikaizen.domain.model.NeuralPhases
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Derives the current neuro-phase and day-of-journey from the user's start date.
 */
class CurrentPhaseUseCase @Inject constructor() {

    data class JourneyState(
        val dayNumber: Int,
        val currentPhase: NeuralPhase,
        val allPhases: List<NeuralPhase>,
        val daysInPhaseRemaining: Int,
        val phaseProgressFraction: Float   // 0f..1f
    )

    /**
     * Calculates the 1-based day of the journey using local calendar days.
     * Day 1 is the calendar day the journey was started.
     */
    fun calculateDayNumber(
        startDateMs: Long,
        nowMs: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Int {
        if (startDateMs <= 0L) return 1
        return try {
            val startLocalDate = Instant.ofEpochMilli(startDateMs).atZone(zoneId).toLocalDate()
            val todayLocalDate = Instant.ofEpochMilli(nowMs).atZone(zoneId).toLocalDate()
            val daysBetween = ChronoUnit.DAYS.between(startLocalDate, todayLocalDate)
            (daysBetween + 1).toInt().coerceAtLeast(1)
        } catch (e: Exception) {
            1
        }
    }

    fun execute(dayNumber: Int, difficulty: DifficultyLevel): JourneyState {
        val safeDay      = dayNumber.coerceAtLeast(1)
        val phases       = NeuralPhases.forDifficulty(difficulty)
        val currentPhase = NeuralPhases.currentPhase(safeDay, difficulty)

        val daysIntoPhase   = safeDay - currentPhase.startDay
        val phaseDuration   = (currentPhase.endDay - currentPhase.startDay + 1)
            .coerceAtLeast(1)
        val fraction        = (daysIntoPhase.toFloat() / phaseDuration).coerceIn(0f, 1f)
        val remaining       = (currentPhase.endDay - safeDay + 1).coerceAtLeast(0)

        return JourneyState(
            dayNumber             = safeDay,
            currentPhase          = currentPhase,
            allPhases             = phases,
            daysInPhaseRemaining  = remaining,
            phaseProgressFraction = fraction
        )
    }

    fun execute(startDateMs: Long, difficulty: DifficultyLevel): JourneyState {
        val dayNumber = calculateDayNumber(startDateMs)
        return execute(dayNumber, difficulty)
    }
}
