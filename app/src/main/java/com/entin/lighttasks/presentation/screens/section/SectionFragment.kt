package com.entin.lighttasks.presentation.screens.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SectionPreferencesBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.screens.dialogs.CreateEditSectionDialog
import com.entin.lighttasks.presentation.screens.dialogs.DeleteSectionDialog
import com.entin.lighttasks.presentation.screens.section.adapter.SectionPreferencesAdapter
import com.entin.lighttasks.presentation.screens.section.adapter.SectionTouchHelperCallback
import com.entin.lighttasks.presentation.util.ZERO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SectionFragment : Fragment(R.layout.section_preferences) {

    private var _binding: SectionPreferencesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SectionViewModel by viewModels()
    private var sectionPreferencesAdapter: SectionPreferencesAdapter? = SectionPreferencesAdapter(
        onEdit = ::openEditSectionDialog,
        onDelete = ::openDeleteSectionDialog,
        updateDb = ::updateAllSections
    )

    private fun updateAllSections(listTasks: List<Section>) {
        viewModel.updateSections(listTasks)
    }

    // Crate / Edit section dialog
    @OptIn(ExperimentalCoroutinesApi::class)
    private val createEditSectionDialog by lazy {
        CreateEditSectionDialog()
    }

    // Delete section dialog
    @OptIn(ExperimentalCoroutinesApi::class)
    private val deleteSectionDialog by lazy {
        DeleteSectionDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = SectionPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSectionsRecyclerView()

        setupSectionsRecyclerViewItemTouchListener()

        setupFabCircleButton()

        stateObserver()
    }

    private fun setupSectionsRecyclerView() {
        binding.sectionPreferencesRecyclerView.apply {
            adapter = sectionPreferencesAdapter
            hasFixedSize()
            setItemViewCacheSize(ZERO)
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupSectionsRecyclerViewItemTouchListener() {
        sectionPreferencesAdapter?.let { adapter ->
            ItemTouchHelper(
                SectionTouchHelperCallback(
                    sectionsAdapterList = adapter,
                    viewModel = viewModel,
                ),
            ).attachToRecyclerView(binding.sectionPreferencesRecyclerView)
        }
    }

    private fun stateObserver() {
        viewModel.sectionEvent.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { event: SectionsEventContract ->
                when (event) {
                    is SectionsEventContract.ShowAllSections -> {
                        sectionPreferencesAdapter?.submitList(event.sections)
                    }
                }
            }.launchIn(lifecycleScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun openCreateSectionDialog() {
        if (!createEditSectionDialog.isVisible) {
            setActualSection(null)
            createEditSectionDialog.show(
                childFragmentManager, CreateEditSectionDialog::class.simpleName
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun openEditSectionDialog(section: Section) {
        if (!createEditSectionDialog.isVisible) {
            setActualSection(section)
            createEditSectionDialog.show(
                childFragmentManager, CreateEditSectionDialog::class.simpleName
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun openDeleteSectionDialog(section: Section) {
        if (!deleteSectionDialog.isVisible) {
            setActualSection(section)
            deleteSectionDialog.show(
                childFragmentManager, CreateEditSectionDialog::class.simpleName
            )
        }
    }

    private fun setupFabCircleButton() {
        binding.fab.setOnClickListener {
            openCreateSectionDialog()
        }
    }

    private fun setActualSection(section: Section?) {
        viewModel.currentSection = section
    }

    override fun onDestroyView() {
        sectionPreferencesAdapter = null
        _binding = null
        super.onDestroyView()
    }
}
