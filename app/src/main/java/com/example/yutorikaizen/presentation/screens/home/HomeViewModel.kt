package com.example.yutorikaizen.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.domain.model.*
import com.example.yutorikaizen.domain.repository.DailyCheckInRepository
import com.example.yutorikaizen.domain.repository.SurgeEventRepository
import com.example.yutorikaizen.domain.repository.UserProfileRepository
import com.example.yutorikaizen.domain.usecase.CurrentPhaseUseCase
import com.example.yutorikaizen.domain.usecase.NciCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.example.yutorikaizen.data.local.SkinPreferences

data class HomeUiState(
    val greeting: String = "Welcome back",
    val dayNumber: Int = 1,
    val currentPhase: NeuralPhase? = null,
    val program: DopamineProgram? = null,
    val nciScore: Float = 0f,
    val currentStreak: Int = 0,
    val logsToday: Int = 0,
    val habitsCompletedToday: Int = 0,
    val totalHabitsToday: Int = 4,
    val onboardingComplete: Boolean = false,
    val isLoading: Boolean = true,
    val hasSeenRibiIntro: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val dailyCheckInRepository: DailyCheckInRepository,
    private val surgeEventRepository: SurgeEventRepository,
    private val currentPhaseUseCase: CurrentPhaseUseCase,
    private val nciCalculator: NciCalculator,
    private val skinPreferences: SkinPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHomeData() }

    private fun loadHomeData() {
        viewModelScope.launch {
            // Also collect the ribi intro flag
            launch {
                skinPreferences.hasSeenRibiIntro.collect { seen ->
                    _uiState.update { it.copy(hasSeenRibiIntro = seen) }
                }
            }

            combine(
                userProfileRepository.observeProfile(),
                skinPreferences.currentJourneyDay
            ) { profile, day -> Pair(profile, day) }
                .collect { (profile, day) ->
                    if (profile == null || !profile.onboardingComplete) {
                        _uiState.update {
                            it.copy(
                                onboardingComplete = false,
                                isLoading = false,
                                dayNumber = 1,
                                currentPhase = null,
                                program = null,
                                nciScore = 0f,
                                currentStreak = 0,
                                habitsCompletedToday = 0,
                                totalHabitsToday = 0
                            )
                        }
                        return@collect
                    }

                    val journeyState = currentPhaseUseCase.execute(day, profile.difficultyLevel)
                    val program      = DopaminePrograms.byId(profile.selectedProgramId)

                    // Today's check-in
                    val todayCheckIn     = dailyCheckInRepository.getForToday()
                    val completedHabits  = todayCheckIn?.completedHabitIds?.size ?: 0
                    val totalHabits      = journeyState.currentPhase.habits.size

                    // Streak
                    val allCheckIns = dailyCheckInRepository.getAll()
                    var streak      = 0
                    for (ci in allCheckIns.reversed()) {
                        if (ci.nciScore >= 0.5f) streak++ else break
                    }

                    // NCI
                    val since14         = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14)
                    val surgeEvents     = surgeEventRepository.getEventsSince(since14)
                    val checkIns14      = allCheckIns.takeLast(14)

                    val checkInEntities = checkIns14.map { ci ->
                        com.example.yutorikaizen.data.local.entity.DailyCheckInEntity(
                            id                = ci.id,
                            epochDay          = ci.epochDay,
                            phaseNumber       = ci.phaseNumber,
                            completedHabitIds = JSONArray(ci.completedHabitIds).toString(),
                            focusMinutes      = ci.focusMinutes,
                            nciScore          = ci.nciScore
                        )
                    }
                    val surgeEntities = surgeEvents.map { se ->
                        com.example.yutorikaizen.data.local.entity.SurgeEventEntity(
                            id              = se.id,
                            timestamp       = se.timestamp,
                            durationSeconds = se.durationSeconds,
                            outcome         = se.outcome.name,
                            methodUsed      = se.methodUsed.name,
                            emotionLogged   = se.emotionLogged
                        )
                    }
                    val nci = nciCalculator.calculate(checkInEntities, surgeEntities, totalHabits)

                    _uiState.update {
                        it.copy(
                            greeting             = buildGreeting(day),
                            dayNumber            = day,
                            currentPhase         = journeyState.currentPhase,
                            program              = program,
                            nciScore             = nci,
                            currentStreak        = streak,
                            habitsCompletedToday = completedHabits,
                            totalHabitsToday     = totalHabits,
                            onboardingComplete   = true,
                            isLoading            = false
                        )
                    }
                }
        }
    }

    private fun buildGreeting(day: Int): String {
        val hour = LocalTime.now().hour
        return when {
            hour < 6  -> "Night Owl Stay strong"
            hour < 12 -> "Good morning! Start strong"
            hour < 17 -> "Keep going, Day $day"
            hour < 21 -> "Evening check-in time"
            else      -> "Winding down. No screens soon"
        }
    }

    fun dismissRibiIntro() {
        viewModelScope.launch {
            skinPreferences.markRibiIntroSeen()
        }
    }
}
