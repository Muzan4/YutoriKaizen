package com.example.yutorikaizen.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.data.local.UsageTracker
import com.example.yutorikaizen.domain.model.AppUsage
import com.example.yutorikaizen.data.local.entity.DailyCheckInEntity
import com.example.yutorikaizen.data.local.entity.SurgeEventEntity
import com.example.yutorikaizen.domain.model.TriggerLog
import com.example.yutorikaizen.domain.repository.AppUsageRepository
import com.example.yutorikaizen.domain.repository.DailyCheckInRepository
import com.example.yutorikaizen.domain.repository.SurgeEventRepository
import com.example.yutorikaizen.domain.repository.TriggerLogRepository
import com.example.yutorikaizen.domain.usecase.NciCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

import com.example.yutorikaizen.domain.model.ScreenTimeTrend
import com.example.yutorikaizen.domain.model.UsageInsight

data class AnalyticsUiState(
    val nciHistory: List<Pair<String, Float>> = emptyList(),
    val triggerFrequency: Map<String, Int> = emptyMap(),
    val recentLogs: List<TriggerLog> = emptyList(),
    val totalResisted: Int = 0,
    val totalYielded: Int = 0,
    val currentStreak: Int = 0,
    val overallNci: Float = 0f,
    val isLoading: Boolean = true,
    val usageInsight: UsageInsight? = null,
    val hasUsagePermission: Boolean = false
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val triggerLogRepository: TriggerLogRepository,
    private val dailyCheckInRepository: DailyCheckInRepository,
    private val surgeEventRepository: SurgeEventRepository,
    private val nciCalculator: NciCalculator,
    private val appUsageRepository: AppUsageRepository,
    private val usageTracker: UsageTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init { loadAnalytics() }

    private fun loadAnalytics() {
        viewModelScope.launch {
            combine(
                triggerLogRepository.observeAll(),
                dailyCheckInRepository.observeLast14Days()
            ) { logs, checkIns -> Pair(logs, checkIns) }
                .collect { (logs, checkIns) ->

                    val freq = logs
                        .groupBy { it.emotion.uppercase() }
                        .mapValues { it.value.size }
                        .entries
                        .sortedByDescending { it.value }
                        .take(5)
                        .associate { it.key to it.value }

                    val nciHistory = checkIns.takeLast(14).map { ci ->
                        val date  = LocalDate.ofEpochDay(ci.epochDay)
                        val label = "${date.monthValue}/${date.dayOfMonth}"
                        label to ci.nciScore
                    }

                    var streak = 0
                    for (ci in checkIns.sortedByDescending { it.epochDay }) {
                        if (ci.nciScore >= 0.5f) streak++ else break
                    }

                    val resisted = surgeEventRepository.countResisted()
                    val yielded  = surgeEventRepository.countYielded()

                    val since14Ms     = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14)
                    val surgeEvents   = surgeEventRepository.getEventsSince(since14Ms)
                    val checkInEnts: List<DailyCheckInEntity> = checkIns.map { ci ->
                        DailyCheckInEntity(
                            id                = ci.id,
                            epochDay          = ci.epochDay,
                            phaseNumber       = ci.phaseNumber,
                            completedHabitIds = JSONArray(ci.completedHabitIds).toString(),
                            focusMinutes      = ci.focusMinutes,
                            nciScore          = ci.nciScore
                        )
                    }
                    val surgeEnts: List<SurgeEventEntity> = surgeEvents.map { se ->
                        SurgeEventEntity(
                            id              = se.id,
                            timestamp       = se.timestamp,
                            durationSeconds = se.durationSeconds,
                            outcome         = se.outcome.name,
                            methodUsed      = se.methodUsed.name,
                            emotionLogged   = se.emotionLogged
                        )
                    }
                    val overallNci = nciCalculator.calculate(checkInEnts, surgeEnts)

                    val hasUsagePermission = usageTracker.hasPermission()
                    var usageInsight: UsageInsight? = null
                    
                    if (hasUsagePermission) {
                        usageTracker.syncUsageForToday()
                        // Instead of a static pull, observe the flows. Wait, we are already in a collect block, but the combine doesn't include usage stats!
                        // Let's just pull the fresh data from the DB manually.
                        
                        val today = LocalDate.now().toEpochDay()
                        val since7Days = today - 6
                        
                        val topApps = appUsageRepository.getTopAppsForDay(today, 5)
                        val totalScreenTimeMillis = appUsageRepository.getTotalScreenTimeForDay(today) ?: 0L
                        val totalScreenTimeMinutes = (totalScreenTimeMillis / 60000).toInt().coerceAtMost(1440)
                        
                        val last7DaysUsage = appUsageRepository.getForDateRange(since7Days, today)
                        val dailyTrendMap = last7DaysUsage.groupBy { it.epochDay }
                            .mapValues { entry -> entry.value.sumOf { it.totalForegroundMinutes } }
                        
                        var total7Days = 0
                        var count7Days = 0
                        val dailyTrend = mutableListOf<Pair<String, Int>>()
                        for (i in 0..6) {
                            val day = since7Days + i
                            val mins = dailyTrendMap[day] ?: 0
                            total7Days += mins
                            count7Days++
                            
                            val date = LocalDate.ofEpochDay(day)
                            val label = "${date.monthValue}/${date.dayOfMonth}"
                            dailyTrend.add(label to mins)
                        }
                        
                        val averageScreenTimeMinutes = if (count7Days > 0) total7Days / count7Days else 0
                        
                        val screenTimeTrend = when {
                            totalScreenTimeMinutes > averageScreenTimeMinutes * 1.1 -> ScreenTimeTrend.UP
                            totalScreenTimeMinutes < averageScreenTimeMinutes * 0.9 -> ScreenTimeTrend.DOWN
                            else -> ScreenTimeTrend.STABLE
                        }
                        
                        val recommendation = when (screenTimeTrend) {
                            ScreenTimeTrend.UP -> "Your screen time is up today. Try a short digital detox."
                            ScreenTimeTrend.DOWN -> "Great job keeping screen time below average!"
                            ScreenTimeTrend.STABLE -> "Your screen time is stable. Keep it up."
                        }
                        
                        usageInsight = UsageInsight(
                            totalScreenTimeMinutes = totalScreenTimeMinutes,
                            averageScreenTimeMinutes = averageScreenTimeMinutes,
                            screenTimeTrend = screenTimeTrend,
                            topApps = topApps,
                            dailyTrend = dailyTrend,
                            recommendation = recommendation
                        )
                    }

                    _uiState.update {
                        it.copy(
                            nciHistory       = nciHistory,
                            triggerFrequency = freq,
                            recentLogs       = logs.take(30),
                            totalResisted    = resisted,
                            totalYielded     = yielded,
                            currentStreak    = streak,
                            overallNci       = overallNci,
                            isLoading        = false,
                            usageInsight     = usageInsight,
                            hasUsagePermission = hasUsagePermission
                        )
                    }
                }
        }
    }

    fun refreshAnalytics() {
        loadAnalytics()
    }
}
