package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.entin.lighttasks.databinding.DeleteSectionDialogBinding
import com.entin.lighttasks.presentation.screens.section.SectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteSectionDialog : DialogFragment() {

    private var _binding: DeleteSectionDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SectionViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = DeleteSectionDialogBinding.inflate(inflater, container, false)

        with(binding) {
            dialogDeleteSectionCancelButton.setOnClickListener {
                dismiss()
            }

            dialogDeleteSectionOkButton.setOnClickListener {
                viewModel.deleteSection()
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
