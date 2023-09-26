package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.ClearFinishedDialogBinding
import com.entin.lighttasks.databinding.LinkAttachedDialogBinding
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteFinishedTasksDialog : DialogFragment() {

    private var _binding: ClearFinishedDialogBinding? = null
    private val binding get() = _binding!!
    private val vm: AllTasksViewModel by activityViewModels()

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
                vm.deleteFinishedTasks() {
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
