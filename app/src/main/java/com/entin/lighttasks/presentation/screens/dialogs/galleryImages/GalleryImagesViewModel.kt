package com.entin.lighttasks.presentation.screens.dialogs.galleryImages

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entin.lighttasks.domain.repository.TasksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for GalleryImagesDialog
 */

@HiltViewModel
class GalleryImagesViewModel @Inject constructor(
    val state: SavedStateHandle,
    private val taskRepository: TasksRepository,
) : ViewModel() {

    val galleryImagesChannel: MutableLiveData<List<Uri>> by lazy {
        MutableLiveData<List<Uri>>()
    }

    /** Get all icons */
    fun getTaskAttachedImages(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            galleryImagesChannel.postValue(
                taskRepository.getAttachedGalleryImagesByTaskId(id).first()
            )
        }
    }

    fun deleteUri(id: Int, imageClickedUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val listUri = taskRepository
                .getAttachedGalleryImagesByTaskId(id)
                .first()
                .toMutableList()
            listUri.removeIf { it == imageClickedUri }

            taskRepository.updateAttachedGalleryImagesByTaskId(id, listUri.toList())
        }
    }
}
