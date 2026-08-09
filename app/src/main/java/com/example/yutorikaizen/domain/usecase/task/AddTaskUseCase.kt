package com.example.yutorikaizen.domain.usecase.task

import com.example.yutorikaizen.domain.repository.TaskRepository
import java.time.LocalDate
import javax.inject.Inject

/** Adds a new task for today's date. */
class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(title: String) {
        repository.addTask(
            title = title.trim(),
            date  = LocalDate.now().toString()   // "YYYY-MM-DD"
        )
    }
}
