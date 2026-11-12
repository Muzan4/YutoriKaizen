package com.example.yutorikaizen.domain.model

data class AppUsage(
    val packageName: String,
    val appLabel: String,
    val epochDay: Long,
    val totalForegroundMinutes: Int,
    val launchCount: Int
)

data class UsageInsight(
    val totalScreenTimeMinutes: Int,
    val averageScreenTimeMinutes: Int,
    val screenTimeTrend: ScreenTimeTrend,
    val topApps: List<AppUsage>,
    val dailyTrend: List<Pair<String, Int>>,
    val recommendation: String
)

enum class ScreenTimeTrend { UP, DOWN, STABLE }
