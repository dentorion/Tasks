package com.entin.lighttasks.domain.repository

import android.net.Uri
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.data.db.entity.TaskEntity
import com.entin.lighttasks.domain.entity.CalendarDatesConstraints
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
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
        sectionId: Int,
    ): Flow<List<Task>>

    fun getNextTaskId(): Flow<Int>

    fun getMaxPosition(): Flow<Int>

    fun newTask(taskEntity: TaskEntity): Flow<Boolean>

    suspend fun updateTask(taskEntity: TaskEntity): Boolean

    suspend fun updateListTask(list: List<TaskEntity>)

    suspend fun deleteTask(task: Task)

    suspend fun deleteFinishedTasks()

    suspend fun getTaskIcons(): List<IconTaskEntity>

    fun getTasksByConstraints(constraints: CalendarDatesConstraints): Flow<List<Task>>

    fun getCountTasksForWidget(): Flow<Int>

    fun getActualPhotoNames(): Flow<List<String>>

    fun getActualAudioRecordsNames(): Flow<List<String>>

    suspend fun updateAllTasksWithDeletedSection(sectionId: Int)

    suspend fun onFinishedTaskClick(id: Int, isFinished: Boolean)

    fun getAttachedGalleryImagesByTaskId(id: Int): Flow<List<Uri>>

    suspend fun updateAttachedGalleryImagesByTaskId(id: Int, listUri: List<Uri>)

    fun getTaskById(taskId: Int): Flow<Task>
}
