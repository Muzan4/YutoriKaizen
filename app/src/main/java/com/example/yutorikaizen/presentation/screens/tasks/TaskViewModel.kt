package com.example.yutorikaizen.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.domain.model.Task
import com.example.yutorikaizen.domain.usecase.task.AddTaskUseCase
import com.example.yutorikaizen.domain.usecase.task.ToggleTaskUseCase
import com.example.yutorikaizen.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val newTaskTitle: String = "",
    val isAtLimit: Boolean = false    // capped at 5 tasks per day
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val addTask: AddTaskUseCase,
    private val toggleTask: ToggleTaskUseCase,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        val today = LocalDate.now().toString()
        viewModelScope.launch {
            taskRepository.observeTasksForDate(today).collect { tasks ->
                _uiState.update {
                    it.copy(tasks = tasks, isAtLimit = tasks.size >= 5)
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(newTaskTitle = value) }
    }

    fun addNewTask() {
        val title = _uiState.value.newTaskTitle.trim()
        if (title.isBlank() || _uiState.value.isAtLimit) return
        viewModelScope.launch {
            addTask(title)
            _uiState.update { it.copy(newTaskTitle = "") }
        }
    }

    fun toggle(task: Task) {
        viewModelScope.launch { toggleTask(task) }
    }
}
