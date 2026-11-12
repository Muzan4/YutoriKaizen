package com.example.yutorikaizen.presentation.screens.surge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.domain.model.SurgeEvent
import com.example.yutorikaizen.domain.model.SurgeMethod
import com.example.yutorikaizen.domain.model.SurgeOutcome
import com.example.yutorikaizen.domain.repository.SurgeEventRepository
import com.example.yutorikaizen.domain.repository.TriggerLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SURGE_DURATION_SECONDS = 90

enum class SurgeTab { BREATHING, CBT, PHYSICAL }

data class SurgeBridgeUiState(
    val secondsRemaining: Int = SURGE_DURATION_SECONDS,
    val isRunning: Boolean = false,
    val isComplete: Boolean = false,
    val survived: Boolean = false,
    val activeTab: SurgeTab = SurgeTab.BREATHING,
    val cbtEmotion: String = "",
    val breathPhase: BreathPhase = BreathPhase.INHALE,
    val breathSeconds: Int = 4,
    val physicalSuggestion: String = ""
)

enum class BreathPhase(val label: String, val durationSeconds: Int) {
    INHALE("Breathe In",  4),
    HOLD1 ("Hold",        4),
    EXHALE("Breathe Out", 4),
    HOLD2 ("Hold",        4)
}

private val PHYSICAL_SUGGESTIONS = listOf(
    "Drink a full glass of cold water 💧",
    "Do 10 slow pushups 💪",
    "Take 5 deep breaths standing up 🌬",
    "Walk to another room and back 🚶",
    "Splash cold water on your face 🧊",
    "Do 15 jumping jacks 🤸",
    "Stretch your arms above your head ✋",
    "Squeeze both hands into fists, then release 🤜"
)

@HiltViewModel
class SurgeBridgeViewModel @Inject constructor(
    private val surgeEventRepository: SurgeEventRepository,
    private val triggerLogRepository: TriggerLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurgeBridgeUiState(
        physicalSuggestion = PHYSICAL_SUGGESTIONS.random()
    ))
    val uiState: StateFlow<SurgeBridgeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var breathJob: Job? = null
    private val startTimeMs = System.currentTimeMillis()

    init { startTimer() }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true) }
            while (_uiState.value.secondsRemaining > 0) {
                delay(1000)
                _uiState.update { it.copy(secondsRemaining = it.secondsRemaining - 1) }
            }
            _uiState.update { it.copy(isRunning = false, isComplete = true) }
        }
        startBreathCycle()
    }

    private fun startBreathCycle() {
        breathJob?.cancel()
        breathJob = viewModelScope.launch {
            val phases = BreathPhase.entries
            var idx    = 0
            while (true) {
                val phase = phases[idx % phases.size]
                _uiState.update { it.copy(breathPhase = phase, breathSeconds = phase.durationSeconds) }
                repeat(phase.durationSeconds) {
                    delay(1000)
                    _uiState.update { it.copy(breathSeconds = it.breathSeconds - 1) }
                }
                idx++
            }
        }
    }

    fun setTab(tab: SurgeTab) = _uiState.update { it.copy(activeTab = tab) }

    fun onCbtEmotionChange(text: String) = _uiState.update { it.copy(cbtEmotion = text) }

    fun markSurvived(onSaved: () -> Unit) {
        val elapsed = ((System.currentTimeMillis() - startTimeMs) / 1000).toInt()
        val method  = when (_uiState.value.activeTab) {
            SurgeTab.BREATHING -> SurgeMethod.BREATHING
            SurgeTab.CBT       -> SurgeMethod.CBT
            SurgeTab.PHYSICAL  -> SurgeMethod.PHYSICAL
        }
        saveAndClose(SurgeOutcome.RESISTED, elapsed, method, onSaved)
    }

    fun markYielded(onSaved: () -> Unit) {
        val elapsed = ((System.currentTimeMillis() - startTimeMs) / 1000).toInt()
        saveAndClose(SurgeOutcome.YIELDED, elapsed, SurgeMethod.NONE, onSaved)
    }

    private fun saveAndClose(
        outcome: SurgeOutcome,
        elapsed: Int,
        method: SurgeMethod,
        onSaved: () -> Unit
    ) {
        timerJob?.cancel()
        breathJob?.cancel()
        val emotion = _uiState.value.cbtEmotion.takeIf { it.isNotBlank() }
        _uiState.update { it.copy(survived = outcome == SurgeOutcome.RESISTED, isComplete = true) }
        viewModelScope.launch {
            surgeEventRepository.saveEvent(
                SurgeEvent(
                    timestamp       = startTimeMs,
                    durationSeconds = elapsed,
                    outcome         = outcome,
                    methodUsed      = method,
                    emotionLogged   = emotion
                )
            )
            // Also log the trigger for the Analytics Urge Log tab
            triggerLogRepository.saveLog(
                timestamp = startTimeMs,
                emotion   = emotion ?: "UNCLEAR", // Default to UNCLEAR if they didn't use the CBT tab
                note      = if (outcome == SurgeOutcome.RESISTED) "Surge Bridge used and resisted! \uD83D\uDEE1\uFE0F" else "Yielded to the urge."
            )
            onSaved()
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        breathJob?.cancel()
    }
}
