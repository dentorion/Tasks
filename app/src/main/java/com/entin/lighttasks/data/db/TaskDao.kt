package com.entin.lighttasks.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    // SORT_BY_TITLE

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, task_title ASC")
    fun getTasksSortedByTitleAsc(search: String, hideFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, task_title DESC")
    fun getTasksSortedByTitleDesc(search: String, hideFinished: Boolean): Flow<List<Task>>

    // SORT_BY_DATE

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, date_created ASC")
    fun getTasksSortedByDateCreatedAsc(search: String, hideFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, date_created DESC")
    fun getTasksSortedByDateCreatedDesc(search: String, hideFinished: Boolean): Flow<List<Task>>

    // SORT_BY_IMPORTANT

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, task_important ASC")
    fun getTasksSortedByImportantAsc(search: String, hideFinished: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_finished ASC, task_important DESC")
    fun getTasksSortedByImportantDesc(search: String, hideFinished: Boolean): Flow<List<Task>>

    // SORT_BY_MANUAL

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0) AND task_title LIKE '%' || :search || '%' ORDER BY task_position ASC ")
    fun getTasksSortedByManualAsc(search: String, hideFinished: Boolean): Flow<List<Task>>

    // SORT_BY_ICON

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0 AND task_group = :groupId) AND task_title LIKE '%' || :search || '%' ORDER BY task_title ASC ")
    fun getTasksSortedByIconAsc(search: String, hideFinished: Boolean, groupId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (task_finished != :hideFinished OR task_finished = 0 AND task_group = :groupId) AND task_title LIKE '%' || :search || '%' ORDER BY task_title DESC")
    fun getTasksSortedByIconDesc(search: String, hideFinished: Boolean, groupId: Int): Flow<List<Task>>

    // Insert new Task with replace

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun newTask(task: Task): Long

    // Update task or list of tasks

    @Update
    suspend fun updateTask(task: Task): Int

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
