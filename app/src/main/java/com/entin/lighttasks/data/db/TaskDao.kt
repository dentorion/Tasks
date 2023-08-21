package com.entin.lighttasks.data.db

import androidx.room.*
import com.entin.lighttasks.domain.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /**
     * Sorting queries
     * Find ALL in TASKS where TASK TITLE like QUERY doesn't matter where it placed,
     * in the beginning or end of string with order by TASK FINISHED + another field.
     *
     * If hideFinished
     * is TRUE -> show uncompleted.
     * is FALSE -> show all completed + uncompleted.
     */

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, task_title")
    fun getTasksSortedByTitle(search: String, hideFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, date_created")
    fun getTasksSortedByDateCreated(search: String, hideFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, task_important DESC")
    fun getTasksSortedByImportant(search: String, hideFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_position ASC ")
    fun getTasksSortedByManual(search: String, hideFinished: Boolean): Flow<List<Task>>

    // Insert new Task with replace

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun newTask(task: Task): Long

    // Update task or list of tasks

    @Update
    suspend fun updateTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAllTasks(list: List<Task>)

    // Deleting queries

    @Delete
    suspend fun deleteTask(task: Task): Int

    @Query("DELETE FROM tasks WHERE task_finished = 1")
    suspend fun deleteFinishedTasks()

    // Find a maximum value of position among the list of tasks

    @Query("SELECT MAX(task_position) FROM tasks")
    fun getLastId(): Flow<Int?>
}