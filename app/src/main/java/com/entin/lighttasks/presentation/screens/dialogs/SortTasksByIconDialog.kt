package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.entin.lighttasks.databinding.SortTaskDialogBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SortTasksByIconDialog : DialogFragment() {

    private var _binding: SortTaskDialogBinding? = null
    private val binding get() = _binding!!
    private val args: SortTasksByIconDialogArgs by navArgs()
    private val viewModel: AllTasksViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = SortTaskDialogBinding.inflate(inflater, container, false)

        val task: Task = args.task
        val icon = getIconTaskDrawable(task)

        with(binding) {
            dialogSortTaskIconToShow.setImageResource(icon)

            dialogSortTaskCancelButton.setOnClickListener {
                dismiss()
            }

            dialogSortTaskOkButton.setOnClickListener {
                viewModel.onTaskSortByIcon(task)
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
