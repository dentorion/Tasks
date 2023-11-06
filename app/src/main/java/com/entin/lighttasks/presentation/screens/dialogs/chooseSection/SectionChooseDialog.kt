package com.entin.lighttasks.presentation.screens.dialogs.chooseSection

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SectionChooseDialogBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.presentation.screens.addedit.adapter.SectionChooseAdapter
import com.entin.lighttasks.presentation.util.BUNDLE_SECTION_CHOOSE
import com.entin.lighttasks.presentation.util.SUCCESS_CHOOSE_SECTION_RESULT
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.ZERO_LONG
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SectionChooseDialog : DialogFragment() {

    private var _binding: SectionChooseDialogBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var sectionsRepository: SectionsRepository
    private var sectionChooseAdapter: SectionChooseAdapter = SectionChooseAdapter { section ->
        requireParentFragment().setFragmentResult(
            SUCCESS_CHOOSE_SECTION_RESULT,
            bundleOf(BUNDLE_SECTION_CHOOSE to section)
        )
        dismiss()
    }

    fun newInstance(): SectionChooseDialog =
        SectionChooseDialog()

    private fun setDialogWidth(width: Double) {
        val newWidth = (resources.displayMetrics.widthPixels * width).toInt()
        dialog?.window?.setLayout(newWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun hideSystemUI() {
        dialog?.let { dialog ->
            dialog.window?.let { window ->
                view?.let { view ->
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, view).systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.window?.apply {
                attributes?.windowAnimations = R.style.NoPaddingDialogTheme
                setGravity(Gravity.CENTER)
            }
            setDialogWidth(if (isOrientationLandscape(context)) LANDSCAPE_MODE else FULL_SCREEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        isCancelable = false
        _binding = SectionChooseDialogBinding.inflate(inflater, container, false)

        setupSectionsRecyclerView()
        stateObserver()

        binding.sectionChooseClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun setupSectionsRecyclerView() {
        binding.sectionChooseRecyclerView.apply {
            adapter = sectionChooseAdapter
            hasFixedSize()
            setItemViewCacheSize(ZERO)
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        }
    }

    private fun stateObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                sectionsRepository.getAllSections().first().also { listSection ->
                    // Add first "No category" item to list
                    val newListWithNoSection = listSection.toMutableList()
                    newListWithNoSection.add(
                        Section(
                            id = ZERO,
                            title = requireContext().resources.getString(R.string.no_section),
                            createdAt = ZERO_LONG,
                            editedAt = ZERO_LONG,
                            icon = ZERO,
                            isImportant = false,
                            position = ZERO,
                            hasPassword = false,
                        )
                    )
                    sectionChooseAdapter.submitList(newListWithNoSection)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.sectionChooseRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.65
    }
}
