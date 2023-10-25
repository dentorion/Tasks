package com.entin.lighttasks.presentation.screens.section

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.data.db.entity.SecurityEntity
import com.entin.lighttasks.data.util.datastore.Preferences
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.entity.toSectionEntity
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.SecurityRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.EMPTY_STRING
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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

    var sectionTitle: String = EMPTY_STRING
    var sectionIcon: Int = ZERO
    var sectionImportant: Boolean = false
    var currentSectionEntity: Section? = null
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
                currentSectionEntity!!.copy(
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
    fun deleteSection() {
        viewModelScope.launch(Dispatchers.IO) {
            currentSectionEntity?.let {
                sectionRepository.deleteSectionById(it.id)
                taskRepository.updateAllTasksWithDeletedSection(it.id)
                preferences.updateSection(ZERO)
            }
        }
    }

    fun checkPasswordForSectionById(sectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            securityRepository.getSecurityItemBySectionId(sectionId).first()?.id?.let {
                sectionEvent.postValue(
                    SectionsEventContract.CheckPassword(sectionId = sectionId, securityItemId = it)
                )
            }
        }
    }

    fun checkPasswordForSectionDeletionById(section: Section) {
        viewModelScope.launch(Dispatchers.IO) {
            securityRepository.getSecurityItemBySectionId(section.id).first()?.id?.let {
                sectionEvent.postValue(
                    SectionsEventContract.CheckPasswordDeletion(
                        section = section,
                        securityItemId = it
                    )
                )
            }
        }
    }

    fun setPassword(password: String, sectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            securityRepository.getSecurityItemBySectionId(sectionId).first()?.password?.let {
                securityRepository.updateSecurityItemBySectionId(
                    sectionId = sectionId,
                    password = password,
                )
            } ?: kotlin.run {
                securityRepository.addSecurityItem(
                    SecurityEntity(password = password, taskId = ZERO, sectionId = sectionId)
                )
            }
        }
    }

    fun deletePassword(sectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            securityRepository.deleteSecurityItemBySectionId(sectionId)
        }
    }

    fun getSecurityItemIdBySectionId(id: Int): Int? {
        TODO("Not yet implemented")
    }
}
