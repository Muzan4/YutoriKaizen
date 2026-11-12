package com.example.yutorikaizen.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.data.local.SkinPreferences
import com.example.yutorikaizen.domain.model.DifficultyLevel
import com.example.yutorikaizen.domain.model.DopamineProgram
import com.example.yutorikaizen.domain.model.DopaminePrograms
import com.example.yutorikaizen.domain.model.UserProfile
import com.example.yutorikaizen.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val step: Int = 0,                                  // 0=Welcome, 1=Program, 2=Difficulty, 3=Triggers
    val selectedProgram: DopamineProgram? = null,
    val selectedDifficulty: DifficultyLevel? = null,
    val selectedTriggers: Set<String> = emptySet(),
    val isSaving: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val skinPreferences: SkinPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    val programs: List<DopamineProgram> = DopaminePrograms.all

    fun selectProgram(program: DopamineProgram) {
        _uiState.update { it.copy(
            selectedProgram    = program,
            selectedDifficulty = program.recommendedDifficulty
        ) }
    }

    fun selectDifficulty(level: DifficultyLevel) {
        _uiState.update { it.copy(selectedDifficulty = level) }
    }

    fun toggleTrigger(trigger: String) {
        _uiState.update { state ->
            val updated = if (trigger in state.selectedTriggers)
                state.selectedTriggers - trigger
            else
                state.selectedTriggers + trigger
            state.copy(selectedTriggers = updated)
        }
    }

    fun nextStep() {
        _uiState.update { it.copy(step = it.step + 1) }
    }

    fun prevStep() {
        _uiState.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    }

    fun completeOnboarding(onDone: () -> Unit) {
        val state = _uiState.value
        val program   = state.selectedProgram   ?: return
        val difficulty = state.selectedDifficulty ?: return

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            skinPreferences.setCurrentJourneyDay(1)
            userProfileRepository.saveProfile(
                UserProfile(
                    startDate          = System.currentTimeMillis(),
                    difficultyLevel    = difficulty,
                    selectedTriggers   = state.selectedTriggers.toList(),
                    selectedProgramId  = program.id,
                    onboardingComplete = true
                )
            )
            onDone()
        }
    }
}
