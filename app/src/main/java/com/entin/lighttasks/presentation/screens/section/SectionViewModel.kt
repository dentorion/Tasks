package com.entin.lighttasks.presentation.screens.section

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.entity.toSectionEntity
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.SecurityRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.ZERO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SectionViewModel @Inject constructor(
    val state: SavedStateHandle,
    private val sectionRepository: SectionsRepository,
    private val securityRepository: SecurityRepository,
    private val taskRepository: TasksRepository,
    private val preferences: Preferences,
    @Named("AppScopeDI") private val diAppScope: CoroutineScope,
) : ViewModel() {

    val sectionEvent: MutableLiveData<SectionsEventContract> by lazy {
        MutableLiveData<SectionsEventContract>()
    }

    private val _iconTaskEntityChannel = Channel<List<IconTaskEntity>>()
    val iconTaskChannel = _iconTaskEntityChannel.receiveAsFlow()

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
    fun onSaveButtonClick(
        section: Section,
        isUpdate: Boolean
    ) {
        if (isUpdate) {
            updateSection(section.copy(editedAt = Date().time))
        } else {
            createSection(section) // position will be replaced in Repository
        }
    }

    private fun createSection(section: Section) {
        diAppScope.launch {
            sectionRepository.createSection(section.toSectionEntity())
        }
    }

    /**
     * Edit section
     */
    private fun updateSection(section: Section) {
        viewModelScope.launch(Dispatchers.IO) {
            sectionRepository.updateSection(section.toSectionEntity())
        }
    }

    /**
     * Edit list of sections
     */
    fun updateSections(listSection: List<Section>) {
        diAppScope.launch(Dispatchers.IO) {
            sectionRepository.updateSections(
                listSection.map { it.toSectionEntity() }
            )
        }
    }

    /**
     * Delete section
     */
    fun deleteSection(sectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            sectionRepository.getSectionById(sectionId).first().also { section ->
                sectionRepository.deleteSectionById(section.id)
                taskRepository.updateAllTasksWithDeletedSection(section.id)
                preferences.updateSection(ZERO)
            }
        }
    }

    fun checkPasswordForSectionById(sectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            sectionEvent.postValue(
                SectionsEventContract.CheckPassword(sectionId = sectionId)
            )
        }
    }

    fun checkPasswordForSectionDeletionById(section: Section) {
        viewModelScope.launch(Dispatchers.IO) {
            sectionEvent.postValue(
                SectionsEventContract.CheckPasswordDeletion(sectionId = section.id)
            )
        }
    }

    fun deletePassword(sectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            securityRepository.deleteSecurityItemBySectionId(sectionId)
        }
    }
}
