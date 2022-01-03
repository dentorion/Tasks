package com.entin.lighttasks.data.repositoryImpl

import com.entin.lighttasks.data.db.TaskDao
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.RemoteTasksRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.TASKS
import com.entin.lighttasks.presentation.util.getUserUid
import com.entin.lighttasks.presentation.util.toDomainModel
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

/**
 * Repository Implementation of [TasksRepository] interface
 */

class RemoteTasksRepositoryImpl @Inject constructor(
    private val tasksDao: TaskDao,
    @Named(TASKS) private val fireBaseDb: CollectionReference,
) : RemoteTasksRepository {

    /**
     * Actual list of remote Tasks
     */
    private val currentRemoteTasks = mutableListOf<Task>()

    /**
     * Save
     */

    // List of Tasks to Firebase
    override suspend fun saveLocalTasksToFirebase(): Flow<Boolean> = flow {
        tasksDao.getAllTasks().first().also { list ->
            saveEachTask(list = list, userUid = getUserUid(), flowCollector = this)
        }
    }.catch {
        emit(false)
    }.flowOn(Dispatchers.IO)

    // Single task to Firebase
    override suspend fun saveSingleTaskToRemote(task: Task): Flow<Boolean> = flow {
        saveEachTask(list = listOf(task), userUid = getUserUid(), flowCollector = this)
    }.flowOn(Dispatchers.IO)

    /**
     * Delete Task from Firebase
     */
    override suspend fun deleteRemoteTask(task: Task): Flow<Boolean> = flow {
        var result = false
        fireBaseDb.document(getUserUid()).collection(TASKS).document(task.date.toString()).delete()
            .addOnCompleteListener {
                result = it.isSuccessful
            }.await()
        emit(result)
    }.flowOn(Dispatchers.IO)

    /**
     * Get all remote Tasks by userUid
     */
    override suspend fun getAllRemoteTasks(): Flow<Result<List<Task>>> =
        flow {
            currentRemoteTasks.removeAll(currentRemoteTasks)

            fireBaseDb.document(getUserUid()).collection(TASKS).get().await()
                .onEach { document ->
                    currentRemoteTasks.add(document.toDomainModel())
                }

            emit(Result.success(currentRemoteTasks.toList()))
        }.catch {
            emit(Result.failure(IOException("download error!")))
        }.flowOn(Dispatchers.IO)

    // UTIL FUNCTIONS

    private suspend fun saveEachTask(
        list: List<Task>,
        userUid: String,
        flowCollector: FlowCollector<Boolean>
    ) {
        if (list.isNotEmpty()) {
            list.forEach { task ->
                fireBaseDb.document(userUid).collection(TASKS).document(task.date.toString())
                    .set(task).await()
            }
            flowCollector.emit(true)
        } else {
            flowCollector.emit(false)
        }
    }
}