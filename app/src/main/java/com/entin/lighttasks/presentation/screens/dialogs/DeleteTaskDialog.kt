package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.entin.lighttasks.databinding.DeleteTaskDialogBinding
import com.entin.lighttasks.domain.entity.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteTaskDialog : DialogFragment() {

    private var _binding: DeleteTaskDialogBinding? = null
    private val binding get() = _binding!!

    private var task: Task? = null
    private var onTaskSwipedDelete: ((Task) -> Unit)? = null

    fun newInstance(task: Task, onDelete: (Task) -> Unit): DeleteTaskDialog =
        DeleteTaskDialog().apply {
            this.task = task
            this.onTaskSwipedDelete = onDelete
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = DeleteTaskDialogBinding.inflate(inflater, container, false)

        with(binding) {
            dialogDeleteTaskCancelButton.setOnClickListener {
                dismiss()
            }

            dialogDeleteTaskOkButton.setOnClickListener {
                onTaskSwipedDelete?.let { onSwipe ->
                    task?.let { task ->
                        onSwipe(task)
                    }
                    dismiss()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
