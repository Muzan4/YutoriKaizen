package com.example.yutorikaizen.domain.usecase

import com.example.yutorikaizen.data.local.entity.DailyCheckInEntity
import com.example.yutorikaizen.data.local.entity.SurgeEventEntity
import javax.inject.Inject

/**
 * Calculates the Neuro-Consistency Index (NCI) — a rolling score 0f..1f
 * representing the user's habit consistency over the last 14 days.
 *
 * Formula:
 *  NCI = (0.4 × habit_ratio) + (0.35 × surge_resist_ratio) + (0.25 × focus_ratio)
 *
 * where:
 *  habit_ratio        = completed habits / total expected habits (14-day window)
 *  surge_resist_ratio = resisted surges / total surges (14-day window)
 *  focus_ratio        = days with ≥ 15 min focus / 14
 */
class NciCalculator @Inject constructor() {

    fun calculate(
        checkIns: List<DailyCheckInEntity>,
        surgeEvents: List<SurgeEventEntity>,
        expectedHabitsPerDay: Int = 4
    ): Float {
        if (checkIns.isEmpty()) return 0f

        val days = checkIns.size.coerceAtLeast(1)

        // 1. Habit completion ratio
        val totalCompletedHabits = checkIns.sumOf { parseHabitCount(it.completedHabitIds) }
        val totalExpectedHabits  = days * expectedHabitsPerDay
        val habitRatio           = (totalCompletedHabits.toFloat() / totalExpectedHabits).coerceIn(0f, 1f)

        // 2. Surge resistance ratio
        val totalSurges   = surgeEvents.size
        val resistedSurges = surgeEvents.count { it.outcome == "RESISTED" }
        val surgeRatio    = if (totalSurges == 0) 0.8f  // no surges = assumed controlled
                            else (resistedSurges.toFloat() / totalSurges).coerceIn(0f, 1f)

        // 3. Focus ratio: days with >= 15 min focus
        val goodFocusDays = checkIns.count { it.focusMinutes >= 15 }
        val focusRatio    = (goodFocusDays.toFloat() / days).coerceIn(0f, 1f)

        return (0.40f * habitRatio) + (0.35f * surgeRatio) + (0.25f * focusRatio)
    }

    private fun parseHabitCount(json: String): Int {
        // Simple count: count comma-separated items in JSON array
        if (json.isBlank() || json == "[]") return 0
        return json.count { it == '"' } / 2
    }
}
