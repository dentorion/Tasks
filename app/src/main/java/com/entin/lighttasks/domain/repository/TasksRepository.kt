package com.entin.lighttasks.domain.repository

import com.entin.lighttasks.domain.entity.CalendarDatesConstraints
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.entity.IconTask
import kotlinx.coroutines.flow.Flow

/**
 * Interface of Repository
 */

interface TasksRepository {

    fun getAllTasksWithSorting(
        query: String,
        orderSort: OrderSort,
        hideFinished: Boolean,
        isAsc: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    fun getMaxPosition(): Flow<Int>

    fun newTask(task: Task): Flow<Boolean>

    suspend fun updateTask(task: Task): Boolean

    suspend fun deleteTask(task: Task)

    suspend fun deleteFinishedTasks()

    suspend fun updateAllTasks(list: List<Task>)

    suspend fun getTaskIconGroups(): List<IconTask>

    fun getTasksByConstraints(constraints: CalendarDatesConstraints): Flow<List<Task>>

    fun getCountTasksForWidget(): Flow<Int>
}
