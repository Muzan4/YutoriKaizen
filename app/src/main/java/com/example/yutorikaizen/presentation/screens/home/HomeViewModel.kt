package com.example.yutorikaizen.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.domain.model.FrogState
import com.example.yutorikaizen.domain.repository.FocusSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "Good morning",
    val frogState: FrogState = FrogState.CONTENT,
    val consistencyScore: Float = 0.5f,
    val todaySessionsCount: Int = 0,
    val showScoreDetails: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(greeting = buildGreeting()) }
        observeTodaySessions()
    }

    private fun observeTodaySessions() {
        viewModelScope.launch {
            focusSessionRepository.observeAllSessions().collect { sessions ->
                val today = LocalDate.now().toString()
                val todayCompleted = sessions.count { session ->
                    session.completed &&
                    java.time.Instant.ofEpochMilli(session.startTime)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .toString() == today
                }

                val rawScore = (todayCompleted * 15).coerceAtMost(100) / 100f
                val score = if (rawScore == 0f) 0.30f else rawScore

                val frogState = when {
                    score < 0.40f -> FrogState.SLEEPY
                    score < 0.70f -> FrogState.CONTENT
                    else          -> FrogState.HAPPY
                }

                _uiState.update {
                    it.copy(
                        todaySessionsCount = todayCompleted,
                        consistencyScore   = score,
                        frogState          = frogState
                    )
                }
            }
        }
    }

    fun toggleScoreDetails() {
        _uiState.update { it.copy(showScoreDetails = !it.showScoreDetails) }
    }

    private fun buildGreeting(): String {
        val hour = LocalTime.now().hour
        return when {
            hour < 12 -> "Good morning 🌅"
            hour < 17 -> "Good afternoon ☀️"
            hour < 21 -> "Good evening 🌙"
            else      -> "Good night 🌙"
        }
    }
}
