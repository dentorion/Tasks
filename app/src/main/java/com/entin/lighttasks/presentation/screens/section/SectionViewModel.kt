package com.entin.lighttasks.presentation.screens.section

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SectionViewModel @Inject constructor(
    val state: SavedStateHandle,
    private val sectionRepository: SectionsRepository,
    private val taskRepository: TasksRepository,
    private val preferences: Preferences,
    @Named("AppScopeDI") private val diAppScope: CoroutineScope,
) : ViewModel() {

    val sectionEvent: MutableLiveData<SectionsEventContract> by lazy {
        MutableLiveData<SectionsEventContract>()
    }

    private val _iconTaskEntityChannel = Channel<List<IconTaskEntity>>()
    val iconTaskChannel = _iconTaskEntityChannel.receiveAsFlow()

    var sectionTitle: String = EMPTY_STRING
    var sectionIcon: Int = ZERO
    var sectionImportant: Boolean = false
    var currentSectionEntity: SectionEntity? = null
        set(value) {
            field = value
            sectionTitle = value?.title ?: EMPTY_STRING
            sectionIcon = value?.icon ?: ZERO
            sectionImportant = value?.isImportant ?: false
        }

    init {
        getAllSections()
    }

    /**
     * Get all sections
     */
    private fun getAllSections() {
        viewModelScope.launch(Dispatchers.IO) {
            sectionRepository.getAllSections().collect { listOfSections ->
                sectionEvent.postValue(SectionsEventContract.ShowAllSections(listOfSections))
            }
        }
    }

    /** Get all groups for task to show icons */
    fun getIcons() {
        viewModelScope.launch(Dispatchers.IO) {
            _iconTaskEntityChannel.send(taskRepository.getTaskIcons().shuffled())
        }
    }

    /**
     * Create section
     */
    fun onSaveButtonClick() {
        if (currentSectionEntity == null) {
            createSection(
                SectionEntity(
                    id = ZERO,
                    title = sectionTitle,
                    createdAt = System.currentTimeMillis(),
                    editedAt = ZERO_LONG,
                    icon = sectionIcon,
                    isImportant = sectionImportant,
                    position = ZERO, // will be replaced in Repository
                )
            )
        } else {
            updateSection(
                currentSectionEntity!!.copy(
                    title = sectionTitle,
                    editedAt = System.currentTimeMillis(),
                    icon = sectionIcon,
                    isImportant = sectionImportant,
                )
            )
        }
    }

    private fun createSection(sectionEntity: SectionEntity) {
        diAppScope.launch {
            sectionRepository.createSection(sectionEntity)
        }
    }

    /**
     * Edit section
     */
    private fun updateSection(sectionEntity: SectionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            sectionRepository.updateSection(sectionEntity)
        }
    }

    /**
     * Edit list of sections
     */
    fun updateSections(sectionEntities: List<SectionEntity>) {
        diAppScope.launch(Dispatchers.IO) {
            sectionRepository.updateSections(sectionEntities)
        }
    }

    /**
     * Delete section
     */
    fun deleteSection() {
        viewModelScope.launch(Dispatchers.IO) {
            currentSectionEntity?.let {
                sectionRepository.deleteSection(it)
                taskRepository.updateAllTasksWithDeletedSection(it.id)
                preferences.updateSection(ZERO)
            }
        }
    }
}
