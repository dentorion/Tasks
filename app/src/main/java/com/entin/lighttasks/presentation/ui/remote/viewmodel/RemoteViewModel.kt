package com.entin.lighttasks.presentation.ui.remote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.RemoteTasksRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.ui.remote.contract.RemoteViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoteViewModel @Inject constructor(
    private val repository: TasksRepository,
    private val remoteRepository: RemoteTasksRepository,
) : ViewModel() {

    private val _remoteTasksViewState: MutableStateFlow<RemoteViewState> =
        MutableStateFlow(RemoteViewState.Idle)
    val remoteTasksViewState = _remoteTasksViewState.asStateFlow()

    private var currentRemoteTasks = listOf<Task>()

    /**
     * Save local tasks to Remote Tasks
     */
    fun saveLocalTasksToRemote() = viewModelScope.launch {
        _remoteTasksViewState.value = RemoteViewState.Loading

        remoteRepository.saveLocalTasksToFirebase().first().also { result ->
            _remoteTasksViewState.value = RemoteViewState.Inform.SaveLocalTasksToFirebase(result)
            loadFromRemoteTasks(isLoadShow = false)
        }
    }

    /**
     * Load to Recycler tasks from Remote
     */
    fun loadFromRemoteTasks(isLoadShow: Boolean = true) = viewModelScope.launch {
        if (isLoadShow) _remoteTasksViewState.value = RemoteViewState.Loading

        remoteRepository.getAllRemoteTasks().first().also { result ->
            result.onSuccess { tasks ->
                currentRemoteTasks = tasks
                _remoteTasksViewState.value = RemoteViewState.Success(listRemoteTasks = tasks)
            }.onFailure {
                _remoteTasksViewState.value = RemoteViewState.Inform.GetListOfRemoteTasksFailure
            }
        }
    }

    /**
     * Load to Db ALL tasks from Remote.
     * Takes list [currentRemoteTasks] and check each Task for existing in Room DB.
     * Save Remote Task into Room DB only if task is not exist yet.
     */
    fun loadToDbAllRemoteTasks() = viewModelScope.launch(Dispatchers.IO) {
        _remoteTasksViewState.value = RemoteViewState.Loading

        var counterSuccess = 0
        currentRemoteTasks.forEach { task ->
            setTaskPosition(task)
            repository.newTask(task).first().also { result ->
                if (result) counterSuccess++
            }
        }
        _remoteTasksViewState.value =
            if (counterSuccess > 0) {
                RemoteViewState.Inform.LoadListOfRemoteTasks(true)
            } else {
                RemoteViewState.Inform.LoadListOfRemoteTasks(false)
            }
    }

    /**
     * Load to Db ONE (selected) task from Remote
     */
    fun loadToDbSingleRemoteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        _remoteTasksViewState.value = RemoteViewState.Loading

        repository.newTask(task).first().also { result ->
            _remoteTasksViewState.value = if (result) {
                RemoteViewState.Inform.LoadRemoteTask(true)
            } else {
                RemoteViewState.Inform.LoadRemoteTask(false)
            }
        }
    }

    /**
     * Delete from Remote task
     */
    fun deleteRemoteTask(task: Task) = viewModelScope.launch {
        _remoteTasksViewState.value = RemoteViewState.Loading

        remoteRepository.deleteRemoteTask(task).first().also { result ->
            _remoteTasksViewState.value = RemoteViewState.Inform.DeleteRemoteTask(result)
            loadFromRemoteTasks()
        }
    }

    // UTIL FUNCTIONS

    /**
     * Set position to Task.
     * Takes maximum of existing and increment by 1.
     */
    private suspend fun setTaskPosition(task: Task) {
        task.position = repository.getMaxPosition().first() + 1
    }

    /**
     * This viewModel is created by ActivityViewModel
     * and it's ViewState should be "cleaned" to Load state
     * not to show snackBar with old message on next open RemoteFragment.
     */
    fun clearViewState() {
        _remoteTasksViewState.value = RemoteViewState.Idle
    }
}