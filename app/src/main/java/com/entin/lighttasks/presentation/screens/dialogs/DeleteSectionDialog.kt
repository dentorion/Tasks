package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.entin.lighttasks.databinding.DeleteSectionDialogBinding
import com.entin.lighttasks.presentation.screens.dialogs.security.Security
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityDialog
import com.entin.lighttasks.presentation.screens.section.SectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class DeleteSectionDialog : DialogFragment() {

    private var _binding: DeleteSectionDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SectionViewModel by viewModels(ownerProducer = { requireParentFragment() })
    private var sectionId: Int? = null

    fun newInstance(
        sectionId: Int,
    ): DeleteSectionDialog = DeleteSectionDialog().apply {
        this.sectionId = sectionId
        this.arguments = bundleOf(ARG_SECTION_DELETE to sectionId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        sectionId?.let {
            outState.putInt(ARG_SECTION_DELETE, it)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (savedInstanceState != null) {
            this.sectionId = arguments?.getInt(ARG_SECTION_DELETE)
        }

        isCancelable = false
        _binding = DeleteSectionDialogBinding.inflate(inflater, container, false)

        with(binding) {
            dialogDeleteSectionCancelButton.setOnClickListener {
                dismiss()
            }

            dialogDeleteSectionOkButton.setOnClickListener {
                sectionId?.let { idSection ->
                    viewModel.deleteSection(idSection)
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

    companion object {
        const val ARG_SECTION_DELETE = "ARG_SECTION_DELETE"
    }
}
