package com.example.yutorikaizen.presentation.screens.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.domain.usecase.focus.CompleteFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimerUiState(
    val durationSeconds: Int = 25 * 60,
    val secondsRemaining: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false
)

@HiltViewModel
class FocusTimerViewModel @Inject constructor(
    private val completeFocusSession: CompleteFocusSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    /** Epoch millis recorded when the user taps Start, so we know session start time. */
    private var sessionStartTime: Long = 0L

    fun startTimer(durationMinutes: Int = 25) {
        timerJob?.cancel()
        sessionStartTime = System.currentTimeMillis()
        val totalSeconds = durationMinutes * 60
        _uiState.update {
            it.copy(
                durationSeconds  = totalSeconds,
                secondsRemaining = totalSeconds,
                isRunning        = true,
                isPaused         = false,
                isCompleted      = false
            )
        }
        runTimer(durationMinutes)
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, isPaused = true) }
    }

    fun resumeTimer() {
        _uiState.update { it.copy(isRunning = true, isPaused = false) }
        runTimer(_uiState.value.durationSeconds / 60)
    }

    /** Stops the timer early and saves the session as NOT completed (abandoned). */
    fun stopTimer() {
        timerJob?.cancel()
        val durationMinutes = _uiState.value.durationSeconds / 60
        if (sessionStartTime > 0L) {
            viewModelScope.launch {
                completeFocusSession(
                    startTime       = sessionStartTime,
                    durationMinutes = durationMinutes,
                    completed       = false
                )
            }
        }
        sessionStartTime = 0L
        _uiState.update {
            it.copy(
                secondsRemaining = it.durationSeconds,
                isRunning        = false,
                isPaused         = false,
                isCompleted      = false
            )
        }
    }

    private fun runTimer(durationMinutes: Int) {
        timerJob = viewModelScope.launch {
            while (_uiState.value.secondsRemaining > 0) {
                delay(1000L)
                _uiState.update {
                    val next = it.secondsRemaining - 1
                    if (next <= 0) {
                        // Session finished — save to database
                        saveCompletedSession(durationMinutes)
                        it.copy(secondsRemaining = 0, isRunning = false, isCompleted = true)
                    } else {
                        it.copy(secondsRemaining = next)
                    }
                }
            }
        }
    }

    private fun saveCompletedSession(durationMinutes: Int) {
        viewModelScope.launch {
            completeFocusSession(
                startTime       = sessionStartTime,
                durationMinutes = durationMinutes,
                completed       = true
            )
            sessionStartTime = 0L
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
