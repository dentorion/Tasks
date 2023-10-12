package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.entin.lighttasks.databinding.DeleteTaskDialogBinding
import com.entin.lighttasks.data.db.entity.TaskEntity
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteTaskDialog : DialogFragment() {

    private var _binding: DeleteTaskDialogBinding? = null
    private val binding get() = _binding!!
    private val args: DeleteTaskDialogArgs by navArgs()
    private val vmLocal: AllTasksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = DeleteTaskDialogBinding.inflate(inflater, container, false)

        val task: Task = args.task

        with(binding) {
            dialogDeleteTaskCancelButton.setOnClickListener {
                dismiss()
            }

            dialogDeleteTaskOkButton.setOnClickListener {
                vmLocal.onTaskSwipedDelete(task)
                dismiss()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
