package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.entin.lighttasks.databinding.ClearFinishedDialogBinding
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteFinishedTasksDialog : DialogFragment() {

    private var _binding: ClearFinishedDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AllTasksViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = ClearFinishedDialogBinding.inflate(inflater, container, false)

        with(binding) {
            dialogClearFinishedCancelButton.setOnClickListener {
                dismiss()
            }

            dialogClearFinishedOkButton.setOnClickListener {
                viewModel.deleteFinishedTasks() {
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
