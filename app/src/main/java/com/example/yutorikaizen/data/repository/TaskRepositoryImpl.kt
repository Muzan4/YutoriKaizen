package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.TaskDao
import com.example.yutorikaizen.data.local.entity.TaskEntity
import com.example.yutorikaizen.domain.model.Task
import com.example.yutorikaizen.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {

    override suspend fun addTask(title: String, date: String): Long =
        dao.insert(TaskEntity(title = title, date = date))

    override suspend fun toggleComplete(task: Task) =
        dao.update(
            TaskEntity(
                id        = task.id,
                title     = task.title,
                date      = task.date,
                completed = !task.completed    // flip the flag
            )
        )

    override suspend fun deleteTask(task: Task) =
        dao.delete(TaskEntity(id = task.id, title = task.title, date = task.date, completed = task.completed))

    override fun observeTasksForDate(date: String): Flow<List<Task>> =
        dao.observeTasksForDate(date).map { list ->
            list.map { e -> Task(id = e.id, title = e.title, date = e.date, completed = e.completed) }
        }
}
