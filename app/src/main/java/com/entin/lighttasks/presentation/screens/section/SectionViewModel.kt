package com.entin.lighttasks.presentation.screens.section

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.entity.Section
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

    private val _sectionEvent = Channel<SectionsEventContract>()
    val sectionEvent = _sectionEvent.receiveAsFlow()

    private val _iconTaskChannel = Channel<List<IconTask>>()
    val iconTaskChannel = _iconTaskChannel.receiveAsFlow()

    var sectionTitle: String = EMPTY_STRING
    var sectionIcon: Int = ZERO
    var sectionImportant: Boolean = false
    var currentSection: Section? = null
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
                _sectionEvent.send(SectionsEventContract.ShowAllSections(listOfSections))
            }
        }
    }

    /** Get all groups for task to show icons */
    fun getIcons() {
        viewModelScope.launch(Dispatchers.IO) {
            _iconTaskChannel.send(taskRepository.getTaskIcons().shuffled())
        }
    }

    /**
     * Create section
     */
    fun onSaveButtonClick() {
        if (currentSection == null) {
            createSection(
                Section(
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
                currentSection!!.copy(
                    title = sectionTitle,
                    editedAt = System.currentTimeMillis(),
                    icon = sectionIcon,
                    isImportant = sectionImportant,
                )
            )
        }
    }

    private fun createSection(section: Section) {
        diAppScope.launch {
            sectionRepository.createSection(section)
        }
    }

    /**
     * Edit section
     */
    private fun updateSection(section: Section) {
        viewModelScope.launch(Dispatchers.IO) {
            sectionRepository.updateSection(section)
        }
    }

    /**
     * Edit list of sections
     */
    fun updateSections(sections: List<Section>) {
        diAppScope.launch(Dispatchers.IO) {
            sectionRepository.updateSections(sections)
        }
    }

    /**
     * Delete section
     */
    fun deleteSection() {
        viewModelScope.launch(Dispatchers.IO) {
            currentSection?.let {
                sectionRepository.deleteSection(it)
                taskRepository.updateAllTasksWithDeletedSection(it.id)
                preferences.updateSection(ZERO)
            }
        }
    }
}
