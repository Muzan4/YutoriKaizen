package com.example.yutorikaizen.presentation.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.domain.model.*
import com.example.yutorikaizen.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HabitTrackerUiState(
    val isLoading: Boolean = true,
    val habitsWithLogs: List<HabitWithLog> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingHabit: Habit? = null,
    val todayCompletedCount: Int = 0,
    val todayTotalCount: Int = 0
)

@HiltViewModel
class HabitTrackerViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitTrackerUiState())
    val uiState: StateFlow<HabitTrackerUiState> = _uiState.asStateFlow()

    private val today = LocalDate.now()
    private val todayEpochDay = today.toEpochDay()

    init { loadHabits() }

    private fun loadHabits() {
        viewModelScope.launch {
            combine(
                habitRepository.observeActiveHabits(),
                habitRepository.observeLogsForDay(todayEpochDay)
            ) { habits, todayLogs ->
                val todayLogMap = todayLogs.associateBy { it.habitId }
                habits.map { habit ->
                    val todayLog = todayLogMap[habit.id]
                    val recentLogs = habitRepository.getRecentLogs(habit.id, 30)
                    val last7 = (0..6).map { daysAgo ->
                        val day = today.minusDays(daysAgo.toLong()).toEpochDay()
                        recentLogs.any { it.epochDay == day && it.completedCount >= habit.targetCount }
                    }.reversed()
                    // Calculate streak from end of last7 towards beginning
                    var streak = 0
                    for (i in last7.indices.reversed()) {
                        if (last7[i]) streak++ else break
                    }
                    val totalDays = habitRepository.getTotalCompletedDays(habit.id)
                    HabitWithLog(habit = habit, todayLog = todayLog, streak = streak, totalDays = totalDays, last7Days = last7)
                }
            }.collect { habitsWithLogs ->
                val completed = habitsWithLogs.count { hwl ->
                    hwl.todayLog != null && hwl.todayLog.completedCount >= hwl.habit.targetCount
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        habitsWithLogs = habitsWithLogs,
                        todayCompletedCount = completed,
                        todayTotalCount = habitsWithLogs.size
                    )
                }
            }
        }
    }

    fun toggleHabit(habit: Habit) {
        viewModelScope.launch {
            val existing = habitRepository.getLogForHabitOnDay(habit.id, todayEpochDay)
            val newCount = if (existing == null || existing.completedCount < habit.targetCount) {
                (existing?.completedCount ?: 0) + 1
            } else {
                0 // reset if already fully done
            }
            habitRepository.upsertLog(
                HabitLog(
                    id = existing?.id ?: 0,
                    habitId = habit.id,
                    epochDay = todayEpochDay,
                    completedCount = newCount,
                    loggedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun showAddDialog() { _uiState.update { it.copy(showAddDialog = true, editingHabit = null) } }
    fun showEditDialog(habit: Habit) { _uiState.update { it.copy(showAddDialog = true, editingHabit = habit) } }
    fun dismissDialog() { _uiState.update { it.copy(showAddDialog = false, editingHabit = null) } }

    fun saveHabit(
        name: String,
        description: String,
        emoji: String,
        colorHex: String,
        isGoodHabit: Boolean,
        frequency: HabitFrequency,
        targetCount: Int,
        unit: String
    ) {
        viewModelScope.launch {
            val editing = _uiState.value.editingHabit
            if (editing != null) {
                habitRepository.update(editing.copy(
                    name = name, description = description, emoji = emoji,
                    colorHex = colorHex, isGoodHabit = isGoodHabit,
                    frequency = frequency, targetCount = targetCount, unit = unit
                ))
            } else {
                habitRepository.insert(Habit(
                    name = name, description = description, emoji = emoji,
                    colorHex = colorHex, isGoodHabit = isGoodHabit,
                    frequency = frequency, targetCount = targetCount, unit = unit
                ))
            }
            dismissDialog()
        }
    }

    fun archiveHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.update(habit.copy(isActive = false))
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            habitRepository.delete(habit)
        }
    }
}
