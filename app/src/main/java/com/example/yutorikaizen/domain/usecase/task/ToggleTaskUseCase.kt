package com.example.yutorikaizen.domain.usecase.task

import com.example.yutorikaizen.domain.model.Task
import com.example.yutorikaizen.domain.repository.TaskRepository
import javax.inject.Inject

/** Toggles a task between completed and not-completed. */
class ToggleTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = repository.toggleComplete(task)
}
