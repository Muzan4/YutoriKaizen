package com.example.yutorikaizen.presentation.screens.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.data.local.SkinPreferences
import com.example.yutorikaizen.data.local.entity.DailyCheckInEntity
import com.example.yutorikaizen.data.local.entity.SurgeEventEntity
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
import java.time.LocalDate
import java.util.concurrent.TimeUnit
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

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val dailyCheckInRepository: DailyCheckInRepository,
    private val surgeEventRepository: SurgeEventRepository,
    private val currentPhaseUseCase: CurrentPhaseUseCase,
    private val nciCalculator: NciCalculator,
    private val skinPreferences: SkinPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(JourneyUiState())
    val uiState: StateFlow<JourneyUiState> = _uiState.asStateFlow()

    init { observeJourney() }

    private fun observeJourney() {
        viewModelScope.launch {
            combine(
                userProfileRepository.observeProfile(),
                skinPreferences.currentJourneyDay,
                dailyCheckInRepository.observeLast14Days()
            ) { profile, day, checkIns -> Triple(profile, day, checkIns) }
                .collect { (profile, day, checkIns) ->
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

                    val program = DopaminePrograms.byId(profile.selectedProgramId)
                    val state   = currentPhaseUseCase.execute(day, profile.difficultyLevel)

                    // Today's check-in
                    val todayEpochDay = LocalDate.now().toEpochDay()
                    val todayCheckIn = checkIns.firstOrNull { it.epochDay == todayEpochDay }
                        ?: dailyCheckInRepository.getForToday()
                    val completed    = todayCheckIn?.completedHabitIds?.toSet() ?: emptySet()

                    // 14-day NCI: map domain → entity for calculator
                    val checkInEntities: List<DailyCheckInEntity> = checkIns.map { ci ->
                        DailyCheckInEntity(
                            id                = ci.id,
                            epochDay          = ci.epochDay,
                            phaseNumber       = ci.phaseNumber,
                            completedHabitIds = JSONArray(ci.completedHabitIds).toString(),
                            focusMinutes      = ci.focusMinutes,
                            nciScore          = ci.nciScore
                        )
                    }

                    // Surge events last 14 days
                    val since14Ms = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(14)
                    val surgeEntityList: List<SurgeEventEntity> = surgeEventRepository
                        .getEventsSince(since14Ms)
                        .map { se ->
                            SurgeEventEntity(
                                id              = se.id,
                                timestamp       = se.timestamp,
                                durationSeconds = se.durationSeconds,
                                outcome         = se.outcome.name,
                                methodUsed      = se.methodUsed.name,
                                emotionLogged   = se.emotionLogged
                            )
                        }

                    val nci = nciCalculator.calculate(
                        checkInEntities,
                        surgeEntityList,
                        state.currentPhase.habits.size.coerceAtLeast(1)
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
                            nciScore              = nci,
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
            dailyCheckInRepository.upsert(
                DailyCheckIn(
                    id                = existing?.id ?: 0,
                    epochDay          = LocalDate.now().toEpochDay(),
                    phaseNumber       = _uiState.value.currentPhase?.number ?: 1,
                    completedHabitIds = completed.toList(),
                    focusMinutes      = existing?.focusMinutes ?: 0,
                    nciScore          = _uiState.value.nciScore
                )
            )
        }
    }
}
