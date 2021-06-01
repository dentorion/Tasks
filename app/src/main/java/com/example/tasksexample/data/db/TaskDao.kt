package com.example.tasksexample.data.db

import androidx.room.*
import com.example.tasksexample.domain.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE (task_finished != :showFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, task_title")
    fun getTasksSortedByTitle(search: String, showFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :showFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, date_created")
    fun getTasksSortedByDateCreated(search: String, showFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :showFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_important DESC, task_finished")
    fun getTasksSortedByImportant(search: String, showFinished: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE task_finished = 1")
    suspend fun deleteFinishedTasks()
}