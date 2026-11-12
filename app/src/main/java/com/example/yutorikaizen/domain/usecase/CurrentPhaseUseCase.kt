package com.example.yutorikaizen.domain.usecase

import com.example.yutorikaizen.domain.model.DifficultyLevel
import com.example.yutorikaizen.domain.model.NeuralPhase
import com.example.yutorikaizen.domain.model.NeuralPhases
import java.util.concurrent.TimeUnit
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
        val nowMs        = System.currentTimeMillis()
        val dayNumber    = ((nowMs - startDateMs) / TimeUnit.DAYS.toMillis(1)).toInt() + 1
        return execute(dayNumber.coerceAtLeast(1), difficulty)
    }
}
