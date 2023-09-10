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

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND title LIKE '%' || :search || '%' ORDER BY is_finished ASC, title ASC")
    fun getTasksSortedByTitleAsc(search: String, hideFinished: Boolean, hideDatePick: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND title LIKE '%' || :search || '%' ORDER BY is_finished ASC, title DESC")
    fun getTasksSortedByTitleDesc(search: String, hideFinished: Boolean, hideDatePick: Boolean): Flow<List<Task>>

    // SORT_BY_DATE

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND title LIKE '%' || :search || '%' ORDER BY is_finished ASC, date_created ASC")
    fun getTasksSortedByDateCreatedAsc(search: String, hideFinished: Boolean, hideDatePick: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND title LIKE '%' || :search || '%' ORDER BY is_finished ASC, date_created DESC")
    fun getTasksSortedByDateCreatedDesc(search: String, hideFinished: Boolean, hideDatePick: Boolean): Flow<List<Task>>

    // SORT_BY_IMPORTANT

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND title LIKE '%' || :search || '%' ORDER BY is_finished ASC, is_important ASC")
    fun getTasksSortedByImportantAsc(search: String, hideFinished: Boolean, hideDatePick: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND title LIKE '%' || :search || '%' ORDER BY is_finished ASC, is_important DESC")
    fun getTasksSortedByImportantDesc(search: String, hideFinished: Boolean, hideDatePick: Boolean): Flow<List<Task>>

    // SORT_BY_MANUAL

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND title LIKE '%' || :search || '%' ORDER BY position ASC ")
    fun getTasksSortedByManualAsc(search: String, hideFinished: Boolean, hideDatePick: Boolean): Flow<List<Task>>

    // SORT_BY_ICON

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND task_group = :groupId ORDER BY title ASC")
    fun getTasksSortedByIconAsc(hideFinished: Boolean, groupId: Int, hideDatePick: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (is_finished != :hideFinished OR is_finished = 0) AND (is_task_expired != :hideDatePick OR is_task_expired = 0) AND task_group = :groupId ORDER BY title DESC")
    fun getTasksSortedByIconDesc(hideFinished: Boolean, groupId: Int, hideDatePick: Boolean): Flow<List<Task>>

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

    @Query("DELETE FROM tasks WHERE is_finished = 1")
    suspend fun deleteFinishedTasks()

    // Find a maximum value of position among the list of tasks

    @Query("SELECT MAX(position) FROM tasks")
    fun getLastId(): Flow<Int?>
}
