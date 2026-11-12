package com.example.yutorikaizen.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Represents one of the four progressive neuro-phases in the 90-day journey.
 * Phase durations scale with difficulty level.
 */
data class NeuralPhase(
    val number: Int,
    val name: String,
    val subtitle: String,
    val description: String,
    val emoji: String,
    val startDay: Int,
    val endDay: Int,
    val color: Color,
    val habits: List<PhaseHabit>
) {
    val durationDays: Int get() = endDay - startDay + 1
}

data class PhaseHabit(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isRequired: Boolean = true
)

object NeuralPhases {

    /** Returns the 4 phases with correct day ranges for the given difficulty. */
    fun forDifficulty(level: DifficultyLevel): List<NeuralPhase> = when (level) {
        DifficultyLevel.GENTLE     -> buildPhases(14, 45, 75)
        DifficultyLevel.CHALLENGER -> buildPhases(12, 40, 68)
        DifficultyLevel.HARD       -> buildPhases(10, 35, 60)
    }

    fun currentPhase(daysSinceStart: Int, level: DifficultyLevel): NeuralPhase {
        val phases = forDifficulty(level)
        return phases.lastOrNull { daysSinceStart >= it.startDay } ?: phases.first()
    }

    private fun buildPhases(p1End: Int, p2End: Int, p3End: Int): List<NeuralPhase> = listOf(
        NeuralPhase(
            number      = 1,
            name        = "Awareness",
            subtitle    = "Know your triggers",
            description = "Log every urge without judgment. Understand what emotional state is driving your scrolling.",
            emoji       = "",
            startDay    = 1,
            endDay      = p1End,
            color       = Color(0xFF7CB47A),
            habits      = phase1Habits
        ),
        NeuralPhase(
            number      = 2,
            name        = "Friction Gates",
            subtitle    = "Slow the impulse",
            description = "A 10–15 second breathing pause before giving in to any urge. You are rewiring the automatic response.",
            emoji       = "",
            startDay    = p1End + 1,
            endDay      = p2End,
            color       = Color(0xFF5BA4CF),
            habits      = phase2Habits
        ),
        NeuralPhase(
            number      = 3,
            name        = "Habit Stacking",
            subtitle    = "Replace, don't resist",
            description = "Pair every urge with a real-world action — a walk, pushups, cold water. Stack good habits on top of old cravings.",
            emoji       = "",
            startDay    = p2End + 1,
            endDay      = p3End,
            color       = Color(0xFFD4956A),
            habits      = phase3Habits
        ),
        NeuralPhase(
            number      = 4,
            name        = "Stabilization",
            subtitle    = "Lock in the baseline",
            description = "Your brain has rebuilt its reward threshold. Focus on maintaining weekly consistency and long-term identity shifts.",
            emoji       = "",
            startDay    = p3End + 1,
            endDay      = 999,
            color       = Color(0xFF9C7FD4),
            habits      = phase4Habits
        )
    )

    private val phase1Habits = listOf(
        PhaseHabit("p1_log",      "Log every urge",        "When you feel the pull, open the app and log it. No judgment.",           ""),
        PhaseHabit("p1_reflect",  "3-second reflection",   "Before any mindless scroll, pause and ask: what am I avoiding?",          ""),
        PhaseHabit("p1_sleep",    "Phone-free bed",        "Charge your phone outside the bedroom tonight.",                          ""),
        PhaseHabit("p1_morning",  "No phone first 30 min", "Keep your phone face-down for 30 minutes after waking.",                  "")
    )

    private val phase2Habits = listOf(
        PhaseHabit("p2_breathe",  "Box breathing",         "When an urge hits, do one full box breath (4-4-4-4) before acting.",      ""),
        PhaseHabit("p2_delay",    "15-second delay",       "Set a 15-second pause before opening any leisure app.",                   ""),
        PhaseHabit("p2_cbt",      "Daily CBT prompt",      "Answer today's reflection question in the journal.",                      ""),
        PhaseHabit("p2_notif",    "Notifications off",     "Turn off all non-essential notifications for the day.",                   ""),
        PhaseHabit("p2_log",      "Log outcomes",          "Did you resist or yield? Track every attempt.",                           "")
    )

    private val phase3Habits = listOf(
        PhaseHabit("p3_replace",  "Habit replacement",     "Do a 5-min real-world action before any leisure app session.",            ""),
        PhaseHabit("p3_focus",    "15-min focus block",    "Complete a timed focus session before unlocking leisure time.",           ""),
        PhaseHabit("p3_move",     "Move your body",        "10 pushups, a walk, or stretching — once today.",                        ""),
        PhaseHabit("p3_water",    "Hydration anchor",      "Drink a full glass of water the moment you feel an urge.",               ""),
        PhaseHabit("p3_cbt",      "Weekly CBT review",     "Spend 5 minutes reviewing your trigger patterns this week.",             "")
    )

    private val phase4Habits = listOf(
        PhaseHabit("p4_nci",      "Check your NCI score",  "Review your Neuro-Consistency Index weekly and celebrate improvements.", ""),
        PhaseHabit("p4_identity", "Identity affirmation",  "Say aloud: I am someone who chooses presence over distraction.",         ""),
        PhaseHabit("p4_mentor",   "Share your progress",   "Tell one person about your journey or write a reflection note.",         ""),
        PhaseHabit("p4_maintain", "Maintenance log",        "Do one mindful check-in per day — even if it's a 30-second note.",     "")
    )
}
