package com.example.yutorikaizen.domain.model

data class UserProfile(
    val startDate: Long,
    val difficultyLevel: DifficultyLevel,
    val selectedTriggers: List<String>,
    val selectedProgramId: String,
    val onboardingComplete: Boolean
)

enum class DifficultyLevel(val label: String, val emoji: String, val description: String) {
    GENTLE(
        "Gentle",
        "🌱",
        "Soft resets. Short delays, flexible habits. Perfect for beginners."
    ),
    CHALLENGER(
        "Challenger",
        "🔥",
        "Real friction. 15s delays, mandatory focus blocks. For those serious about change."
    ),
    HARD(
        "Hard Mode",
        "⚡",
        "Maximum rewiring. Cold-turkey approach. No shortcuts. For the committed."
    )
}
