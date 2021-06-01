package com.example.tasksexample.domain.repository

import com.example.tasksexample.domain.entity.OrderSort
import com.example.tasksexample.domain.entity.Task
import kotlinx.coroutines.flow.*

interface TasksRepository {

    fun getAllTasksWithSorting(query: String, orderSort: OrderSort, showFinished: Boolean): Flow<List<Task>>

    suspend fun newTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun deleteFinishedTasks()
}