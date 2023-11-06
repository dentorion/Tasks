package com.entin.lighttasks.presentation.screens.dialogs.deleteTask

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.entin.lighttasks.databinding.DeleteTaskDialogBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.util.BUNDLE_TASK_DELETE
import com.entin.lighttasks.presentation.util.DELETE_TASK_RESULT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteTaskDialog : DialogFragment() {

    private var _binding: DeleteTaskDialogBinding? = null
    private val binding get() = _binding!!

    private var task: Task? = null

    fun newInstance(task: Task): DeleteTaskDialog =
        DeleteTaskDialog().apply {
            this.task = task
            this.arguments = bundleOf(ARG_TASK to task)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_TASK, task)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (savedInstanceState != null) {
            this.task = arguments?.getParcelable(ARG_TASK)
        }

        isCancelable = false
        _binding = DeleteTaskDialogBinding.inflate(inflater, container, false)

        with(binding) {
            dialogDeleteTaskCancelButton.setOnClickListener {
                dismiss()
            }

            dialogDeleteTaskOkButton.setOnClickListener {
                task?.let { task ->
                    requireParentFragment().setFragmentResult(
                        DELETE_TASK_RESULT,
                        bundleOf(
                            BUNDLE_TASK_DELETE to task,
                        )
                    )
                }
                dismiss()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_TASK = "ARG_TASK"
    }
}
