package com.entin.lighttasks.data.repository

import com.entin.lighttasks.data.db.TaskDao
import com.entin.lighttasks.data.db.TaskGroupsDao
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.entity.TaskGroup
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.ZERO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Implementation of [TasksRepository] interface
 */

@Singleton
class TasksRepositoryImpl @Inject constructor(
    private val tasksDao: TaskDao,
    private val taskGroupsDao: TaskGroupsDao,
) : TasksRepository {

    /**
     * Getting list of tasks by special order
     */
    override fun getAllTasksWithSorting(
        query: String,
        orderSort: OrderSort,
        hideFinished: Boolean,
        isAsc: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>> =
        when (orderSort) {
            OrderSort.SORT_BY_DATE -> {
                if (isAsc) {
                    tasksDao.getTasksSortedByDateCreatedAsc(query, hideFinished, hideDatePick)
                } else {
                    tasksDao.getTasksSortedByDateCreatedDesc(query, hideFinished, hideDatePick)
                }
            }
            OrderSort.SORT_BY_TITLE -> {
                if (isAsc) {
                    tasksDao.getTasksSortedByTitleAsc(query, hideFinished, hideDatePick)
                } else {
                    tasksDao.getTasksSortedByTitleDesc(query, hideFinished, hideDatePick)
                }
            }
            OrderSort.SORT_BY_ICON -> {
                if(isAsc) {
                    tasksDao.getTasksSortedByIconAsc(hideFinished, orderSort.groupId, hideDatePick)
                } else {
                    tasksDao.getTasksSortedByIconDesc(hideFinished, orderSort.groupId, hideDatePick)
                }
            }
            OrderSort.SORT_BY_IMPORTANT -> {
                if(isAsc) {
                    tasksDao.getTasksSortedByImportantAsc(query, hideFinished, hideDatePick)
                } else {
                    tasksDao.getTasksSortedByImportantDesc(query, hideFinished, hideDatePick)
                }
            }
            OrderSort.SORT_BY_MANUAL -> tasksDao.getTasksSortedByManualAsc(query, hideFinished, hideDatePick)
        }

    /**
     * Create new Task
     */
    override fun newTask(task: Task): Flow<Boolean> = flow {
        emit(tasksDao.newTask(task.copy(position = getMaxPosition().first())) > ZERO)
    }

    /**
     * Updating queries
     */
    override suspend fun updateAllTasks(list: List<Task>) {
        tasksDao.updateAllTasks(list)
    }

    override suspend fun updateTask(task: Task): Boolean {
        return tasksDao.updateTask(task) > ZERO
    }

    /**
     * Delete queries
     */
    override suspend fun deleteFinishedTasks() {
        tasksDao.deleteFinishedTasks()
    }

    override suspend fun deleteTask(task: Task) {
        tasksDao.deleteTask(task)
    }

    /**
     * Get maximum position of tasks table
     */
    override fun getMaxPosition(): Flow<Int> =
        tasksDao.getLastId().map { max -> max?.let { it + ONE } ?: ZERO }

    /**
     * Get images for task groups
     */
    override suspend fun getTaskGroups(): List<TaskGroup> =
        taskGroupsDao.getTaskGroups()
}
