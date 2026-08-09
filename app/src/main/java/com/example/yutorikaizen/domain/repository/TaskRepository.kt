package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(title: String, date: String): Long
    suspend fun toggleComplete(task: Task)
    suspend fun deleteTask(task: Task)
    fun observeTasksForDate(date: String): Flow<List<Task>>
}
