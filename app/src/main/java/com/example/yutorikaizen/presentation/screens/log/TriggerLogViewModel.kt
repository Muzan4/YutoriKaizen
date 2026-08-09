package com.example.yutorikaizen.presentation.screens.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yutorikaizen.domain.model.TriggerLog
import com.example.yutorikaizen.domain.repository.TriggerLogRepository
import com.example.yutorikaizen.domain.usecase.trigger.LogTriggerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// The five urge emotions, shown as chips
val EMOTIONS = listOf("Bored", "Tired", "Anxious", "Lonely", "Avoiding")

data class TriggerLogUiState(
    val logs: List<TriggerLog> = emptyList(),
    val selectedEmotion: String? = null,
    val note: String = "",
    val justSaved: Boolean = false
)

@HiltViewModel
class TriggerLogViewModel @Inject constructor(
    private val logTrigger: LogTriggerUseCase,
    private val repository: TriggerLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TriggerLogUiState())
    val uiState: StateFlow<TriggerLogUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAll().collect { logs ->
                _uiState.update { it.copy(logs = logs) }
            }
        }
    }

    fun selectEmotion(emotion: String) {
        _uiState.update {
            it.copy(selectedEmotion = if (it.selectedEmotion == emotion) null else emotion)
        }
    }

    fun onNoteChange(value: String) {
        _uiState.update { it.copy(note = value) }
    }

    fun saveLog() {
        val emotion = _uiState.value.selectedEmotion ?: return
        viewModelScope.launch {
            logTrigger(emotion = emotion.uppercase(), note = _uiState.value.note.ifBlank { null })
            _uiState.update { it.copy(selectedEmotion = null, note = "", justSaved = true) }
            kotlinx.coroutines.delay(1500)
            _uiState.update { it.copy(justSaved = false) }
        }
    }
}
