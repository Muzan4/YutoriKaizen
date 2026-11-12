package com.example.yutorikaizen.presentation.screens.dailygoal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.data.local.SkinPreferences
import com.example.yutorikaizen.domain.model.*
import com.example.yutorikaizen.domain.repository.DailyCheckInRepository
import com.example.yutorikaizen.domain.repository.UserProfileRepository
import com.example.yutorikaizen.domain.usecase.CurrentPhaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

data class DailyGoalUiState(
    val isLoading: Boolean = true,
    val dayNumber: Int = 1,
    val dateLabel: String = "",
    val morningIntention: String = "",
    val eveningReflection: String = "",
    val identityStatement: String = "",
    val antiTriggerUrge: String = "",
    val antiTriggerAction: String = "",
    val winOfTheDay: String = "",
    val moodRating: Int = 0,      // 1–5, 0 = not set
    val energyRating: Int = 0,    // 1–5, 0 = not set
    val intentionSaved: Boolean = false,
    val reflectionSaved: Boolean = false,
    val antiTriggerSaved: Boolean = false,
    val prepPhone: Boolean = false,
    val prepScreens: Boolean = false,
    val prepStillness: Boolean = false,
    val todayMission: DailyMission? = null,
    val phaseHabitsTotal: Int = 0,
    val phaseHabitsCompleted: Int = 0,
    val currentPhase: NeuralPhase? = null,
    val showIntentionEdit: Boolean = false,
    val showReflectionEdit: Boolean = false,
    val showAntiTriggerEdit: Boolean = false,
    val nciScore: Float = 0f
)

@HiltViewModel
class DailyGoalViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val dailyCheckInRepository: DailyCheckInRepository,
    private val currentPhaseUseCase: CurrentPhaseUseCase,
    private val skinPreferences: SkinPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DailyGoalUiState())
    val uiState: StateFlow<DailyGoalUiState> = _uiState.asStateFlow()

    private val today = LocalDate.now()
    private val todayEpochDay = today.toEpochDay()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            combine(
                userProfileRepository.observeProfile(),
                skinPreferences.currentJourneyDay
            ) { profile, day -> Pair(profile, day) }
                .collect { (profile, day) ->
                    val dateLabel = today.format(
                        DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
                    )

                    val currentPhase: NeuralPhase?
                    val todayMission: DailyMission?
                    val phaseHabitsTotal: Int

                    if (profile != null) {
                        val state = currentPhaseUseCase.execute(day, profile.difficultyLevel)
                        currentPhase = state.currentPhase
                        phaseHabitsTotal = currentPhase.habits.size
                        val program = DopaminePrograms.byId(profile.selectedProgramId)
                        val missions = program?.dailyMissions ?: emptyList()
                        val missionIndex = if (missions.isNotEmpty()) (day - 1) % missions.size else 0
                        todayMission = missions.getOrNull(missionIndex)
                    } else {
                        currentPhase = null
                        phaseHabitsTotal = 0
                        todayMission = null
                    }

                    val checkIn = dailyCheckInRepository.getForToday()
                    val phaseHabitsCompleted = checkIn?.completedHabitIds?.size ?: 0

                    // Load persisted goal and reflection data
                    val intention = skinPreferences.getDailyGoalData(todayEpochDay, "intention") ?: ""
                    val reflection = skinPreferences.getDailyGoalData(todayEpochDay, "reflection") ?: ""
                    val win = skinPreferences.getDailyGoalData(todayEpochDay, "win") ?: ""
                    val identity = skinPreferences.getDailyGoalData(todayEpochDay, "identity") ?: ""
                    val urge = skinPreferences.getDailyGoalData(todayEpochDay, "anti_urge") ?: ""
                    val action = skinPreferences.getDailyGoalData(todayEpochDay, "anti_action") ?: ""
                    val mood = skinPreferences.getDailyGoalData(todayEpochDay, "mood")?.toIntOrNull() ?: 0
                    val energy = skinPreferences.getDailyGoalData(todayEpochDay, "energy")?.toIntOrNull() ?: 0
                    val prepPhone = skinPreferences.getDailyGoalData(todayEpochDay, "prep_phone") == "true"
                    val prepScreens = skinPreferences.getDailyGoalData(todayEpochDay, "prep_screens") == "true"
                    val prepStillness = skinPreferences.getDailyGoalData(todayEpochDay, "prep_stillness") == "true"

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            dayNumber = day,
                            dateLabel = dateLabel,
                            morningIntention = intention,
                            eveningReflection = reflection,
                            winOfTheDay = win,
                            identityStatement = identity,
                            antiTriggerUrge = urge,
                            antiTriggerAction = action,
                            moodRating = mood,
                            energyRating = energy,
                            intentionSaved = intention.isNotBlank(),
                            reflectionSaved = reflection.isNotBlank() || win.isNotBlank(),
                            antiTriggerSaved = urge.isNotBlank() && action.isNotBlank(),
                            prepPhone = prepPhone,
                            prepScreens = prepScreens,
                            prepStillness = prepStillness,
                            todayMission = todayMission,
                            phaseHabitsTotal = phaseHabitsTotal,
                            phaseHabitsCompleted = phaseHabitsCompleted,
                            currentPhase = currentPhase,
                            nciScore = checkIn?.nciScore ?: 0f
                        )
                    }
                }
        }
    }

    fun saveIntention(text: String) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "intention", text)
            _uiState.update {
                it.copy(
                    morningIntention = text,
                    intentionSaved = text.isNotBlank(),
                    showIntentionEdit = false
                )
            }
        }
    }

    fun saveReflection(text: String, win: String) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "reflection", text)
            skinPreferences.saveDailyGoalData(todayEpochDay, "win", win)
            _uiState.update {
                it.copy(
                    eveningReflection = text,
                    winOfTheDay = win,
                    reflectionSaved = text.isNotBlank() || win.isNotBlank(),
                    showReflectionEdit = false
                )
            }
        }
    }

    fun saveAntiTrigger(urge: String, action: String, identity: String) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "anti_urge", urge)
            skinPreferences.saveDailyGoalData(todayEpochDay, "anti_action", action)
            skinPreferences.saveDailyGoalData(todayEpochDay, "identity", identity)
            _uiState.update {
                it.copy(
                    antiTriggerUrge = urge,
                    antiTriggerAction = action,
                    identityStatement = identity,
                    antiTriggerSaved = urge.isNotBlank() && action.isNotBlank(),
                    showAntiTriggerEdit = false
                )
            }
        }
    }

    fun setMood(rating: Int) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "mood", rating.toString())
            _uiState.update { it.copy(moodRating = rating) }
        }
    }

    fun setEnergy(rating: Int) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "energy", rating.toString())
            _uiState.update { it.copy(energyRating = rating) }
        }
    }

    fun togglePrepPhone(enabled: Boolean) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "prep_phone", enabled.toString())
            _uiState.update { it.copy(prepPhone = enabled) }
        }
    }

    fun togglePrepScreens(enabled: Boolean) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "prep_screens", enabled.toString())
            _uiState.update { it.copy(prepScreens = enabled) }
        }
    }

    fun togglePrepStillness(enabled: Boolean) {
        viewModelScope.launch {
            skinPreferences.saveDailyGoalData(todayEpochDay, "prep_stillness", enabled.toString())
            _uiState.update { it.copy(prepStillness = enabled) }
        }
    }

    fun showIntentionEdit() { _uiState.update { it.copy(showIntentionEdit = true) } }
    fun hideIntentionEdit() { _uiState.update { it.copy(showIntentionEdit = false) } }
    fun showReflectionEdit() { _uiState.update { it.copy(showReflectionEdit = true) } }
    fun hideReflectionEdit() { _uiState.update { it.copy(showReflectionEdit = false) } }
    fun showAntiTriggerEdit() { _uiState.update { it.copy(showAntiTriggerEdit = true) } }
    fun hideAntiTriggerEdit() { _uiState.update { it.copy(showAntiTriggerEdit = false) } }
}
