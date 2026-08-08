package com.example.yutorikaizen.domain.model

/**
 * Represents the duck's current emotional/animation state, derived from
 * the user's rolling 7-day Consistency Score.
 *
 * WHY an enum and not an Int?  Because the duck has exactly 3 named states.
 * An enum makes illegal states impossible to represent — you can't accidentally
 * pass a duckState of 5 and cause a crash. The ViewModel maps a Float score
 * to this enum; the UI maps this enum to animation frames. Each layer only
 * needs to know about its own translation.
 *
 * Thresholds (set in GetDuckStateUseCase — not here):
 *   SLEEPY  0–39%   just resting, slow idle bob
 *   CONTENT 40–69%  sitting alert, gentle wing twitch loop
 *   HAPPY   70–100% hopping, wing flap loop
 */
enum class DuckState {
    SLEEPY,
    CONTENT,
    HAPPY
}
