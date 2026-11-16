package com.example.yutorikaizen.domain.usecase

import com.example.yutorikaizen.data.local.entity.DailyCheckInEntity
import com.example.yutorikaizen.data.local.entity.SurgeEventEntity
import javax.inject.Inject

/**
 * Calculates the Daily Score (0f..1f) representing daily performance.
 *
 * Formula:
 *  Daily Score = (0.75 × habit_ratio) + (0.25 × focus_ratio)
 *
 * where:
 *  habit_ratio = completed habits / total expected habits
 *                (tracks both self-made habits and journey section habits)
 *  focus_ratio = completed focus minutes / required focus minutes
 *                (starts at 25 min in Phase 1, gradually increasing throughout the journey:
 *                 Phase 1: 25 min, Phase 2: 30 min, Phase 3: 35 min, Phase 4+: 40 min)
 *
 * No urges are tracked for this score.
 */
class NciCalculator @Inject constructor() {

    /**
     * Calculates the Daily Score for a single day based on habits and deep focus.
     */
    fun calculateDailyScore(
        completedJourneyHabits: Int,
        totalJourneyHabits: Int,
        completedCustomHabits: Int = 0,
        totalCustomHabits: Int = 0,
        focusMinutes: Int = 0,
        phaseNumber: Int = 1
    ): Float {
        val totalHabits = totalJourneyHabits + totalCustomHabits
        val completedHabits = completedJourneyHabits + completedCustomHabits

        val habitRatio = if (totalHabits > 0) {
            (completedHabits.toFloat() / totalHabits).coerceIn(0f, 1f)
        } else 0f

        val requiredFocus = requiredFocusMinutes(phaseNumber)
        val focusRatio = if (requiredFocus > 0) {
            (focusMinutes.toFloat() / requiredFocus).coerceIn(0f, 1f)
        } else 0f

        return ((0.75f * habitRatio) + (0.25f * focusRatio)).coerceIn(0f, 1f)
    }

    /**
     * Required deep focus minutes by journey phase:
     * Phase 1: 25 minutes
     * Phase 2: 30 minutes
     * Phase 3: 35 minutes
     * Phase 4+: 40 minutes
     */
    fun requiredFocusMinutes(phaseNumber: Int): Int = when (phaseNumber) {
        1 -> 25
        2 -> 30
        3 -> 35
        else -> 40
    }

    /**
     * Rolling score over a list of daily check-ins.
     * Surge events are ignored (no urges).
     */
    fun calculate(
        checkIns: List<DailyCheckInEntity>,
        surgeEvents: List<SurgeEventEntity> = emptyList(),
        expectedHabitsPerDay: Int = 4
    ): Float {
        if (checkIns.isEmpty()) return 0f

        val scores = checkIns.map { ci ->
            if (ci.nciScore > 0f) {
                ci.nciScore
            } else {
                val completedHabits = parseHabitCount(ci.completedHabitIds)
                val habitRatio = (completedHabits.toFloat() / expectedHabitsPerDay.coerceAtLeast(1)).coerceIn(0f, 1f)
                val requiredFocus = requiredFocusMinutes(ci.phaseNumber)
                val focusRatio = (ci.focusMinutes.toFloat() / requiredFocus).coerceIn(0f, 1f)
                ((0.75f * habitRatio) + (0.25f * focusRatio)).coerceIn(0f, 1f)
            }
        }
        return (scores.sum() / scores.size).coerceIn(0f, 1f)
    }

    private fun parseHabitCount(json: String): Int {
        if (json.isBlank() || json == "[]") return 0
        return json.count { it == '"' } / 2
    }
}
