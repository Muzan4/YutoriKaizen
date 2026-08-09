package com.example.yutorikaizen.presentation.screens.duckbreak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FrogBreakOption { BREATHING, JOURNAL, PHYSICAL }
enum class BreathPhase { INHALE, HOLD_IN, EXHALE, HOLD_OUT }

val PHYSICAL_NUDGES = listOf(
    "Stand up and stretch for 30 seconds 🧘",
    "Get a glass of water 💧",
    "Take 10 slow, deep breaths 🌬️",
    "Look at something 20 feet away for 20 seconds 👀",
    "Roll your shoulders back 3 times 🔄"
)

data class FrogBreakUiState(
    val selectedOption: FrogBreakOption? = null,
    val breathPhase: BreathPhase = BreathPhase.INHALE,
    val breathLabel: String = "Breathe in…",
    val breathProgress: Float = 0f,
    val secondsInPhase: Int = 4,
    val journalText: String = "",
    val physicalNudge: String = PHYSICAL_NUDGES.first(),
    val isDone: Boolean = false
)

@HiltViewModel
class FrogBreakViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FrogBreakUiState())
    val uiState: StateFlow<FrogBreakUiState> = _uiState.asStateFlow()

    private var breathJob: Job? = null

    fun selectOption(option: FrogBreakOption) {
        _uiState.update { it.copy(selectedOption = option) }
        if (option == FrogBreakOption.BREATHING) startBreathing()
        if (option == FrogBreakOption.PHYSICAL) {
            _uiState.update { it.copy(physicalNudge = PHYSICAL_NUDGES.random()) }
        }
    }

    private fun startBreathing() {
        breathJob?.cancel()
        breathJob = viewModelScope.launch {
            repeat(3) {
                runPhase(BreathPhase.INHALE,   "Breathe in…",    4)
                runPhase(BreathPhase.HOLD_IN,  "Hold…",          4)
                runPhase(BreathPhase.EXHALE,   "Breathe out…",   4)
                runPhase(BreathPhase.HOLD_OUT, "Hold…",          4)
            }
            _uiState.update { it.copy(isDone = true) }
        }
    }

    private suspend fun runPhase(phase: BreathPhase, label: String, durationSec: Int) {
        _uiState.update { it.copy(breathPhase = phase, breathLabel = label, secondsInPhase = durationSec) }
        val steps = durationSec * 10
        for (i in 0..steps) {
            _uiState.update { it.copy(breathProgress = i.toFloat() / steps) }
            delay(100L)
        }
    }

    fun onJournalChange(text: String) {
        _uiState.update { it.copy(journalText = text) }
    }

    fun markDone() {
        _uiState.update { it.copy(isDone = true) }
    }

    fun reset() {
        breathJob?.cancel()
        _uiState.update { FrogBreakUiState() }
    }

    override fun onCleared() {
        super.onCleared()
        breathJob?.cancel()
    }
}
