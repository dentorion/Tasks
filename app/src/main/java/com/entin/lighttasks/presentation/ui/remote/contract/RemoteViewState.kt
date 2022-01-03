package com.entin.lighttasks.presentation.ui.remote.contract

import com.entin.lighttasks.domain.entity.Task
import kotlin.random.Random

sealed class RemoteViewState {
    /**
     * Idle
     */
    object Idle: RemoteViewState()

    /**
     * Loading
     */
    object Loading : RemoteViewState()

    /**
     * Success
     * with firebase task list
     */
    data class Success(
        val listRemoteTasks: List<Task> = listOf(),
    ) : RemoteViewState()

    /**
     * Information message snackBar
     */
    sealed class Inform: RemoteViewState() {
        // Save Single Remote Task to Local
        data class LoadRemoteTask(val value: Boolean): Inform()
        // Save List of Remote Tasks to Local
        data class LoadListOfRemoteTasks(val value: Boolean): Inform()
        // Load list of Remote Tasks from Firebase to RecyclerView
        object GetListOfRemoteTasksFailure: Inform()
        // Save Single / All Local Tasks to Remote
        data class SaveLocalTasksToFirebase(val value: Boolean): Inform()
        // Delete Single Task from Remote
        data class DeleteRemoteTask(val value: Boolean): Inform()
    }

    /**
     * For StateFlow that doesn't emit the same value
     */
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return Random.nextInt()
    }
}