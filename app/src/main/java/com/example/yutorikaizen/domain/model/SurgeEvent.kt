package com.example.yutorikaizen.domain.model

data class SurgeEvent(
    val id: Long = 0,
    val timestamp: Long,
    val durationSeconds: Int,
    val outcome: SurgeOutcome,
    val methodUsed: SurgeMethod,
    val emotionLogged: String?
)

enum class SurgeOutcome { RESISTED, YIELDED, INCOMPLETE }
enum class SurgeMethod  { BREATHING, CBT, PHYSICAL, NONE }
