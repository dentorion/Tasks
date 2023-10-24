package com.entin.lighttasks.presentation.screens.dialogs

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.databinding.SectionChooseDialogBinding
import com.entin.lighttasks.presentation.screens.addedit.adapter.SectionChooseAdapter
import com.entin.lighttasks.presentation.screens.section.SectionViewModel
import com.entin.lighttasks.presentation.screens.section.SectionsEventContract
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.isOrientationLandscape
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SectionChooseDialog : DialogFragment() {

    private var onSelect: ((SectionEntity) -> Unit)? = null

    private var _binding: SectionChooseDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SectionViewModel by viewModels()
    private var sectionChooseAdapter: SectionChooseAdapter? = SectionChooseAdapter { section ->
        onSelect?.let { it(section) }
        dismiss()
    }

    fun setOnSelect(action: (SectionEntity) -> Unit) {
        onSelect = action
    }

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

        setupSectionsRecyclerView()
        stateObserver()
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
        viewModel.sectionEvent.observe(viewLifecycleOwner) { event: SectionsEventContract ->
            when (event) {
                is SectionsEventContract.ShowAllSections -> {
                    // Add first "No category" item to list
                    val newListWithNoSection = event.sectionEntities.toMutableList()
                    newListWithNoSection.add(
                        SectionEntity(
                            id = 0,
                            title = requireContext().resources.getString(R.string.no_section),
                            createdAt = 0,
                            editedAt = 0,
                            icon = 0,
                            isImportant = false,
                            position = 0
                        )
                    )
                    sectionChooseAdapter?.submitList(newListWithNoSection)
                }

                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val FULL_SCREEN = 0.92
        const val LANDSCAPE_MODE = 0.65
    }
}
