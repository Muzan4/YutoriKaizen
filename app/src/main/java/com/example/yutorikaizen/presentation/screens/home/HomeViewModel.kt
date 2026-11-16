package com.example.yutorikaizen.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.data.local.SkinPreferences
import com.example.yutorikaizen.domain.model.*
import com.example.yutorikaizen.domain.repository.DailyCheckInRepository
import com.example.yutorikaizen.domain.repository.HabitRepository
import com.example.yutorikaizen.domain.repository.SurgeEventRepository
import com.example.yutorikaizen.domain.repository.UserProfileRepository
import com.example.yutorikaizen.domain.usecase.CurrentPhaseUseCase
import com.example.yutorikaizen.domain.usecase.NciCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

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

private data class HomeDataBundle(
    val profile: UserProfile?,
    val day: Int,
    val activeHabits: List<Habit>,
    val todayLogs: List<HabitLog>,
    val checkIns: List<DailyCheckIn>
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val dailyCheckInRepository: DailyCheckInRepository,
    private val surgeEventRepository: SurgeEventRepository,
    private val currentPhaseUseCase: CurrentPhaseUseCase,
    private val nciCalculator: NciCalculator,
    private val skinPreferences: SkinPreferences,
    private val habitRepository: HabitRepository
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

            val todayEpochDay = LocalDate.now().toEpochDay()

            combine(
                userProfileRepository.observeProfile(),
                skinPreferences.currentJourneyDay,
                habitRepository.observeActiveHabits(),
                habitRepository.observeLogsForDay(todayEpochDay),
                dailyCheckInRepository.observeLast14Days()
            ) { profile, day, activeHabits, todayLogs, checkIns ->
                HomeDataBundle(profile, day, activeHabits, todayLogs, checkIns)
            }.collect { bundle ->
                val profile = bundle.profile
                val activeHabits = bundle.activeHabits
                val todayLogs = bundle.todayLogs
                val checkIns = bundle.checkIns

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

                val day = if (profile.startDate > 0L) {
                    currentPhaseUseCase.calculateDayNumber(profile.startDate)
                } else {
                    bundle.day
                }
                if (bundle.day != day) {
                    skinPreferences.setCurrentJourneyDay(day)
                }

                val journeyState = currentPhaseUseCase.execute(day, profile.difficultyLevel)
                val program      = DopaminePrograms.byId(profile.selectedProgramId)

                // Today's check-in
                val todayCheckIn = checkIns.firstOrNull { it.epochDay == todayEpochDay }
                    ?: dailyCheckInRepository.getForToday()
                val completedJourneyHabits = todayCheckIn?.completedHabitIds?.size ?: 0
                val totalJourneyHabits = journeyState.currentPhase.habits.size

                // Self-made custom habits
                val todayLogMap = todayLogs.associateBy { it.habitId }
                val completedCustomHabits = activeHabits.count { habit ->
                    val log = todayLogMap[habit.id]
                    log != null && log.completedCount >= habit.targetCount
                }
                val totalCustomHabits = activeHabits.size

                val totalHabitsCompletedToday = completedJourneyHabits + completedCustomHabits
                val totalHabitsToday = totalJourneyHabits + totalCustomHabits
                val focusMinutes = todayCheckIn?.focusMinutes ?: 0

                val dailyScore = nciCalculator.calculateDailyScore(
                    completedJourneyHabits = completedJourneyHabits,
                    totalJourneyHabits = totalJourneyHabits,
                    completedCustomHabits = completedCustomHabits,
                    totalCustomHabits = totalCustomHabits,
                    focusMinutes = focusMinutes,
                    phaseNumber = journeyState.currentPhase.number
                )

                // Streak
                var streak = 0
                for (ci in checkIns.sortedByDescending { it.epochDay }) {
                    val score = if (ci.epochDay == todayEpochDay) dailyScore else ci.nciScore
                    if (score >= 0.5f) streak++ else break
                }

                // Keep today's daily check in record updated with the new Daily Score
                if (todayCheckIn == null || todayCheckIn.nciScore != dailyScore) {
                    dailyCheckInRepository.upsert(
                        DailyCheckIn(
                            id = todayCheckIn?.id ?: 0,
                            epochDay = todayEpochDay,
                            phaseNumber = journeyState.currentPhase.number,
                            completedHabitIds = todayCheckIn?.completedHabitIds ?: emptyList(),
                            focusMinutes = focusMinutes,
                            nciScore = dailyScore
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        greeting             = buildGreeting(day),
                        dayNumber            = day,
                        currentPhase         = journeyState.currentPhase,
                        program              = program,
                        nciScore             = dailyScore,
                        currentStreak        = streak,
                        habitsCompletedToday = totalHabitsCompletedToday,
                        totalHabitsToday     = totalHabitsToday,
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
