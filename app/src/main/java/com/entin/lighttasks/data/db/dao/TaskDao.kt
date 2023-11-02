package com.entin.lighttasks.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.entin.lighttasks.data.db.entity.TaskEntity
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

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND title LIKE '%' || :search || '%' " +
            "ORDER BY is_finished ASC, title ASC",
    )
    fun getTasksSortedByTitleAsc(
        search: String,
        hideFinished: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND title LIKE '%' || :search || '%' " +
            "ORDER BY is_finished ASC, title DESC",
    )
    fun getTasksSortedByTitleDesc(
        search: String,
        hideFinished: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    // SORT_BY_DATE

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND title LIKE '%' || :search || '%' " +
            "ORDER BY is_finished ASC, created_at ASC",
    )
    fun getTasksSortedByDateCreatedAsc(
        search: String,
        hideFinished: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND title LIKE '%' || :search || '%' " +
            "ORDER BY is_finished ASC, created_at DESC",
    )
    fun getTasksSortedByDateCreatedDesc(
        search: String,
        hideFinished: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    // SORT_BY_IMPORTANT

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND title LIKE '%' || :search || '%' " +
            "ORDER BY is_finished ASC, is_important ASC",
    )
    fun getTasksSortedByImportantAsc(
        search: String,
        hideFinished: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND title LIKE '%' || :search || '%' " +
            "ORDER BY is_finished ASC, is_important DESC",
    )
    fun getTasksSortedByImportantDesc(
        search: String,
        hideFinished: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    // SORT_BY_MANUAL

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND title LIKE '%' || :search || '%' " +
            "ORDER BY position ASC",
    )
    fun getTasksSortedByManualAsc(
        search: String,
        hideFinished: Boolean,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    // SORT_BY_ICON

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND task_group = :groupId " +
            "ORDER BY title ASC",
    )
    fun getTasksSortedByIconAsc(
        hideFinished: Boolean,
        groupId: Int,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM Tasks " +
            "LEFT JOIN Alarms ON Tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (is_finished != :hideFinished OR is_finished = 0) " +
            "AND (is_task_expired != :hideDatePick OR is_task_expired = 0) " +
            "AND task_group = :groupId " +
            "ORDER BY title DESC",
    )
    fun getTasksSortedByIconDesc(
        hideFinished: Boolean,
        groupId: Int,
        hideDatePick: Boolean,
    ): Flow<List<Task>>

    // Insert new Task with replace

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun newTask(taskEntity: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun newTasks(taskEntities: List<TaskEntity>): List<Long>

    // Update task or list of tasks

    @Update
    suspend fun updateTask(taskEntity: TaskEntity): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAllTasks(taskEntities: List<TaskEntity>)

    // Deleting queries

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int): Int

    @Query("DELETE FROM tasks WHERE is_finished = 1")
    suspend fun deleteFinishedTasks()

    @Query("SELECT MAX(id) FROM tasks")
    fun getNextTaskId(): Flow<Int?>

    @Query("SELECT MAX(position) FROM tasks")
    fun getLastPosition(): Flow<Int?>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM tasks " +
            "LEFT JOIN Alarms ON tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE expire_date_first >= :startExpireDate " +
            "AND expire_date_first <= :finishExpireDate",
    )
    fun getTasksWithStartExpireDate(
        startExpireDate: Long,
        finishExpireDate: Long,
    ): Flow<List<Task>>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM tasks " +
            "LEFT JOIN Alarms ON tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE expire_date_first >= :startExpireDate " +
            "AND expire_date_first <= :finishExpireDate " +
            "AND task_group = :iconGroup",
    )
    fun getTasksWithStartExpireDateWithIcon(
        startExpireDate: Long,
        finishExpireDate: Long,
        iconGroup: Int,
    ): Flow<List<Task>>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM tasks " +
            "LEFT JOIN Alarms ON tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (expire_date_first >= :startExpireDate AND expire_date_first <= :finishExpireDate) " +
            "AND (expire_date_second >= expire_date_first AND expire_date_second <= :finishExpireDate)",
    )
    fun getTasksWithStartFinishExpireDates(
        startExpireDate: Long,
        finishExpireDate: Long,
    ): Flow<List<Task>>

    @Query(
        "SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM tasks " +
            "LEFT JOIN Alarms ON tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE (expire_date_first >= :startExpireDate AND expire_date_first <= :finishExpireDate) " +
            "AND (expire_date_second >= expire_date_first AND expire_date_second <= :finishExpireDate) " +
            "AND (task_group = :iconGroup)",
    )
    fun getTasksWithStartFinishExpireDatesWithIcon(
        startExpireDate: Long,
        finishExpireDate: Long,
        iconGroup: Int,
    ): Flow<List<Task>>

    /**
     * List of actual photo names
     */
    @Query("SELECT attached_photo FROM tasks WHERE attached_photo IS NOT NULL")
    fun getActualPhotoNames(): Flow<List<String>>

    /**
     * List of actual audio records names
     */
    @Query("SELECT attached_voice FROM tasks WHERE attached_voice IS NOT NULL")
    fun getActualAudioRecordsNames(): Flow<List<String>>

    @Query("UPDATE tasks SET section_id = 0 WHERE section_id = :sectionId")
    suspend fun updateAllTasksWithDeletedSection(sectionId: Int)

    @Query("UPDATE tasks SET is_finished = :finished WHERE id = :id")
    suspend fun onFinishedTaskClick(id: Int, finished: Boolean)

    @Query("SELECT attached_gallery_images FROM tasks WHERE id = :id")
    fun getAttachedGalleryImagesByTaskId(id: Int): Flow<String>

    @Query("UPDATE tasks SET attached_gallery_images = :listUri WHERE id = :id")
    fun updateAttachedGalleryImagesByTaskId(id: Int, listUri: String)

    @Query("SELECT Tasks.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password, " +
            "Alarms.alarm_time AS alarm_time " +
            "FROM tasks " +
            "LEFT JOIN Alarms ON tasks.alarm_id = Alarms.alarm_id " +
            "LEFT JOIN Security ON Tasks.id = Security.task_id " +
            "WHERE Tasks.id = :taskId",
        )
    fun getTaskById(taskId: Int): Flow<Task>

    /**
     * WIDGET
     */
    @Query(
        "SELECT COUNT(*) " +
            "FROM tasks " +
            "WHERE (expire_date_first >= :startExpireDate AND expire_date_first <= :finishExpireDate)",
    )
    fun getCountTasksToday(startExpireDate: Long, finishExpireDate: Long): Flow<Int>
}
