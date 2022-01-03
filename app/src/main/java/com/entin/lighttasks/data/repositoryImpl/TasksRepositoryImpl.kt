package com.entin.lighttasks.data.repositoryImpl

import android.util.Log
import com.entin.lighttasks.data.db.TaskDao
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.TasksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Implementation of [TasksRepository] interface
 */

class TasksRepositoryImpl @Inject constructor(
    private val tasksDao: TaskDao,
) : TasksRepository {

    /**
     * Getting list of tasks by special order
     */
    override fun getAllTasksWithSorting(
        query: String,
        orderSort: OrderSort,
        hideFinished: Boolean,
    ): Flow<List<Task>> =
        when (orderSort) {
            OrderSort.SORT_BY_DATE -> tasksDao.getTasksSortedByDateCreated(query, hideFinished)
            OrderSort.SORT_BY_TITLE -> tasksDao.getTasksSortedByTitle(query, hideFinished)
            OrderSort.SORT_BY_IMPORTANT -> tasksDao.getTasksSortedByImportant(query, hideFinished)
            OrderSort.SORT_BY_MANUAL -> tasksDao.getTasksSortedByManual(query, hideFinished)
        }

    /**
     * Create new Task
     * but first looking for the same Task in Room DB by next fields.
     * Remote Task fields should be equal to Room Db Task to return false.
     * - title
     * - message
     * - created
     * - finished
     * - important
     */
    override suspend fun newTask(task: Task) = flow {
        if (isAllowToSave(task)) {
            Log.i("Eska", "REPOSITORY. newTask(). check: TRUE")
            tasksDao.newTask(task)
            emit(true)
        } else {
            Log.i("Eska", "REPOSITORY. newTask(). check: FALSE")
            emit(false)
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Updating queries
     */
    override suspend fun updateAllTasks(list: List<Task>) {
        tasksDao.updateAllTasks(list)
    }

    override suspend fun updateTask(task: Task) {
        tasksDao.updateTask(task)
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
    override fun getMaxPosition(): Flow<Int> = flow {
        emit(tasksDao.getLastId())
    }

    // PRIVATE FUNCTIONS

    /**
     * Looking for the same Task in Room DB be some fields.
     */
    private fun isAllowToSave(task: Task): Boolean {
        return tasksDao.isTaskExist(
            title = task.title,
            message = task.message,
//            important = task.important,
//            finished = task.finished,
//            created = task.date
        ).not()
    }
}