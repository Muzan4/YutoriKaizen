package com.example.yutorikaizen.presentation.screens.journey

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
import javax.inject.Inject

data class JourneyUiState(
    val isLoading: Boolean = true,
    val dayNumber: Int = 1,
    val currentPhase: NeuralPhase? = null,
    val allPhases: List<NeuralPhase> = emptyList(),
    val daysInPhaseRemaining: Int = 0,
    val phaseProgressFraction: Float = 0f,
    val program: DopamineProgram? = null,
    val todayMission: DailyMission? = null,
    val nciScore: Float = 0f,
    val completedHabitIds: Set<String> = emptySet(),
    val totalDays: Int = 90,
    val weeklyFocuses: List<WeeklyFocus> = emptyList()
)

private data class JourneyDataBundle(
    val profile: UserProfile?,
    val day: Int,
    val checkIns: List<DailyCheckIn>,
    val activeHabits: List<Habit>,
    val todayLogs: List<HabitLog>
)

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val dailyCheckInRepository: DailyCheckInRepository,
    private val surgeEventRepository: SurgeEventRepository,
    private val currentPhaseUseCase: CurrentPhaseUseCase,
    private val nciCalculator: NciCalculator,
    private val skinPreferences: SkinPreferences,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JourneyUiState())
    val uiState: StateFlow<JourneyUiState> = _uiState.asStateFlow()

    init { observeJourney() }

    private fun observeJourney() {
        viewModelScope.launch {
            val todayEpochDay = LocalDate.now().toEpochDay()

            combine(
                userProfileRepository.observeProfile(),
                skinPreferences.currentJourneyDay,
                dailyCheckInRepository.observeLast14Days(),
                habitRepository.observeActiveHabits(),
                habitRepository.observeLogsForDay(todayEpochDay)
            ) { profile, day, checkIns, activeHabits, todayLogs ->
                JourneyDataBundle(profile, day, checkIns, activeHabits, todayLogs)
            }.collect { bundle ->
                val profile = bundle.profile
                val checkIns = bundle.checkIns
                val activeHabits = bundle.activeHabits
                val todayLogs = bundle.todayLogs

                if (profile == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dayNumber = 1,
                            currentPhase = null,
                            todayMission = null,
                            nciScore = 0f,
                            completedHabitIds = emptySet()
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

                val program = DopaminePrograms.byId(profile.selectedProgramId)
                val state   = currentPhaseUseCase.execute(day, profile.difficultyLevel)

                // Today's check-in
                val todayCheckIn = checkIns.firstOrNull { it.epochDay == todayEpochDay }
                    ?: dailyCheckInRepository.getForToday()
                val completed = todayCheckIn?.completedHabitIds?.toSet() ?: emptySet()

                // Self-made habits
                val todayLogMap = todayLogs.associateBy { it.habitId }
                val completedCustomHabits = activeHabits.count { habit ->
                    val log = todayLogMap[habit.id]
                    log != null && log.completedCount >= habit.targetCount
                }

                val dailyScore = nciCalculator.calculateDailyScore(
                    completedJourneyHabits = completed.size,
                    totalJourneyHabits = state.currentPhase.habits.size,
                    completedCustomHabits = completedCustomHabits,
                    totalCustomHabits = activeHabits.size,
                    focusMinutes = todayCheckIn?.focusMinutes ?: 0,
                    phaseNumber = state.currentPhase.number
                )

                // Today's mission (cycle through program missions by day)
                val missions     = program?.dailyMissions ?: emptyList()
                val missionIndex = if (missions.isNotEmpty()) (day - 1) % missions.size else 0
                val todayMission = missions.getOrNull(missionIndex)

                _uiState.update {
                    it.copy(
                        isLoading             = false,
                        dayNumber             = day,
                        currentPhase          = state.currentPhase,
                        allPhases             = state.allPhases,
                        daysInPhaseRemaining  = state.daysInPhaseRemaining,
                        phaseProgressFraction = state.phaseProgressFraction,
                        program               = program,
                        todayMission          = todayMission,
                        nciScore              = dailyScore,
                        completedHabitIds     = completed,
                        totalDays             = program?.durationDays ?: 90,
                        weeklyFocuses         = program?.weeklyFocus ?: emptyList()
                    )
                }
            }
        }
    }

    fun resetJourney() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            skinPreferences.setCurrentJourneyDay(1)
            userProfileRepository.resetJourney(now)
            dailyCheckInRepository.deleteAll()
            surgeEventRepository.deleteAll()
        }
    }

    fun toggleHabit(habitId: String) {
        val current = _uiState.value.completedHabitIds.toMutableSet()
        if (habitId in current) current.remove(habitId) else current.add(habitId)
        _uiState.update { it.copy(completedHabitIds = current) }
        saveCheckIn(current)
    }

    private fun saveCheckIn(completed: Set<String>) {
        viewModelScope.launch {
            val existing = dailyCheckInRepository.getForToday()
            val phaseNum = _uiState.value.currentPhase?.number ?: 1
            val todayEpochDay = LocalDate.now().toEpochDay()

            val activeHabits = habitRepository.observeActiveHabits().firstOrNull() ?: emptyList()
            val todayLogs = habitRepository.getLogsForRange(todayEpochDay, todayEpochDay).associateBy { it.habitId }
            val completedCustomHabits = activeHabits.count { habit ->
                val log = todayLogs[habit.id]
                log != null && log.completedCount >= habit.targetCount
            }
            val focusMinutes = existing?.focusMinutes ?: 0

            val newScore = nciCalculator.calculateDailyScore(
                completedJourneyHabits = completed.size,
                totalJourneyHabits = _uiState.value.currentPhase?.habits?.size ?: 4,
                completedCustomHabits = completedCustomHabits,
                totalCustomHabits = activeHabits.size,
                focusMinutes = focusMinutes,
                phaseNumber = phaseNum
            )

            dailyCheckInRepository.upsert(
                DailyCheckIn(
                    id                = existing?.id ?: 0,
                    epochDay          = todayEpochDay,
                    phaseNumber       = phaseNum,
                    completedHabitIds = completed.toList(),
                    focusMinutes      = focusMinutes,
                    nciScore          = newScore
                )
            )
        }
    }
}
