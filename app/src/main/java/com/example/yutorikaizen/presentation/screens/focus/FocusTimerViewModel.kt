package com.example.yutorikaizen.presentation.screens.focus

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

/**
 * FocusTimerViewModel — handles the state and countdown logic for the Focus Session.
 *
 * WHY Coroutine-based countdown?
 * Kotlin Coroutines are lightweight and automatically tied to the Lifecycle of the ViewModel
 * via `viewModelScope`. When the user navigates away or closes the app, the scope is canceled
 * automatically, preventing memory leaks and background timer drain.
 *
 * WHY Flow over LiveData?
 * Kotlin StateFlow is a native Kotlin API that works seamlessly with Compose's reactive state
 * tracking (via collectAsStateWithLifecycle). It is more performant and requires no Android dependencies.
 */
data class TimerUiState(
    val durationSeconds: Int = 25 * 60,
    val secondsRemaining: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false
)

@HiltViewModel
class FocusTimerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    /**
     * Starts a new timer session.
     * @param durationMinutes Length of the session in minutes (defaults to 25).
     */
    fun startTimer(durationMinutes: Int = 25) {
        timerJob?.cancel()
        val totalSeconds = durationMinutes * 60
        _uiState.update {
            it.copy(
                durationSeconds = totalSeconds,
                secondsRemaining = totalSeconds,
                isRunning = true,
                isPaused = false,
                isCompleted = false
            )
        }
        runTimer()
    }

    /**
     * Pauses the active countdown.
     */
    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, isPaused = true) }
    }

    /**
     * Resumes the paused countdown.
     */
    fun resumeTimer() {
        _uiState.update { it.copy(isRunning = true, isPaused = false) }
        runTimer()
    }

    /**
     * Stops the timer and resets it to its initial state.
     */
    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                secondsRemaining = it.durationSeconds,
                isRunning = false,
                isPaused = false,
                isCompleted = false
            )
        }
    }

    private fun runTimer() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.secondsRemaining > 0) {
                delay(1000L)
                _uiState.update {
                    val nextRemaining = it.secondsRemaining - 1
                    if (nextRemaining <= 0) {
                        it.copy(
                            secondsRemaining = 0,
                            isRunning = false,
                            isCompleted = true
                        )
                    } else {
                        it.copy(secondsRemaining = nextRemaining)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
