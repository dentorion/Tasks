package com.entin.lighttasks.domain.repository

import com.entin.lighttasks.domain.entity.Task
import kotlinx.coroutines.flow.Flow

/**
 * Interface of TasksRepository
 */

interface RemoteTasksRepository {

    /**
     * Save to Firebase all local tasks
     */

    suspend fun saveLocalTasksToFirebase(): Flow<Boolean>

    /**
     * Save to Firebase single task
     */

    suspend fun saveSingleTaskToRemote(task: Task): Flow<Boolean>

    /**
     * Download from Firebase
     */

    suspend fun getAllRemoteTasks(): Flow<Result<List<Task>>>

    /**
     * Delete task from Firebase
     */
    suspend fun deleteRemoteTask(task: Task): Flow<Boolean>

}
