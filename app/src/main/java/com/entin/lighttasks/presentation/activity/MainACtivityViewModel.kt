package com.entin.lighttasks.presentation.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.core.PhotoMaker
import com.entin.lighttasks.presentation.util.core.SoundRecorder
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
    private val tasksRepository: TasksRepository,
    private val sectionsRepository: SectionsRepository,
    private val photoMaker: PhotoMaker,
    private val soundRecorder: SoundRecorder,
    private val preferences: Preferences,
) : ViewModel() {

    private val _tasksEvent = Channel<MainActivityEvent>()
    val tasksEvent = _tasksEvent.receiveAsFlow()

    init {
        updateCount()
    }

    fun updateCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = tasksRepository.getCountTasksForWidget().first()
            _tasksEvent.send(MainActivityEvent.CountTasksWidget(count))
        }
    }

    fun deleteUnusedPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.getActualPhotoNames().first().apply {
                photoMaker.deleteUnusedPhotos(this)
            }
        }
    }

    fun deleteUnusedSoundRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.getActualAudioRecordsNames().first().apply {
                soundRecorder.deleteUnusedSoundRecords(this)
            }
        }
    }

    fun openCommonSectionIfNecessary() {
        viewModelScope.launch(Dispatchers.IO) {
            preferences.preferencesFlow.first().sectionId.also { currentSavedSectionId ->
                if (currentSavedSectionId != ZERO) {
                    sectionsRepository.getSectionById(currentSavedSectionId).first().hasPassword.also { isCurrentSectionHasPassword ->
                        if (isCurrentSectionHasPassword) {
                            preferences.updateSection(ZERO)
                        }
                    }
                }
            }
        }
    }
}
