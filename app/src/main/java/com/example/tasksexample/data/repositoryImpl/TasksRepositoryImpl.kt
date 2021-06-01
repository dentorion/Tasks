package com.example.tasksexample.data.repositoryImpl

import android.util.Log
import com.example.tasksexample.domain.entity.OrderSort
import com.example.tasksexample.data.db.TaskDao
import com.example.tasksexample.domain.entity.Task
import com.example.tasksexample.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksRepositoryImpl @Inject constructor(
    private val tasksDao: TaskDao
) : TasksRepository {

    override fun getAllTasksWithSorting(
        query: String,
        orderSort: OrderSort,
        showFinished: Boolean,
    ): Flow<List<Task>> =
        when (orderSort) {
            OrderSort.SORT_BY_DATE -> tasksDao.getTasksSortedByDateCreated(query, showFinished)
            OrderSort.SORT_BY_TITLE -> tasksDao.getTasksSortedByTitle(query, showFinished)
            OrderSort.SORT_BY_IMPORTANT -> tasksDao.getTasksSortedByImportant(query, showFinished)
        }

    override suspend fun deleteFinishedTasks() {
        tasksDao.deleteFinishedTasks()
    }

    override suspend fun updateTask(task: Task) {
        tasksDao.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        tasksDao.deleteTask(task)
    }

    override suspend fun newTask(task: Task) {
        tasksDao.newTask(task)
    }

}