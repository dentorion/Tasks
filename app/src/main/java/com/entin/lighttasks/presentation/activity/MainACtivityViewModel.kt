package com.entin.lighttasks.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.ImageCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: TasksRepository,
    private val imageCache: ImageCache,
) : ViewModel() {

    private val _tasksEvent = Channel<MainActivityEvent>()
    val tasksEvent = _tasksEvent.receiveAsFlow()

    init {
        updateCount()
    }

    fun updateCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = repository.getCountTasksForWidget().first()
            _tasksEvent.send(MainActivityEvent.CountTasksWidget(count))
        }
    }

    fun deleteUnusedPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getActualPhotoNames().first().apply {
                imageCache.deleteUnusedPhotos(this)
            }
        }
    }
}
