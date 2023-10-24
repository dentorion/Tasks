package com.entin.lighttasks.presentation.screens.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.databinding.SectionPreferencesBinding
import com.entin.lighttasks.presentation.screens.dialogs.CreateEditSectionDialog
import com.entin.lighttasks.presentation.screens.dialogs.DeleteSectionDialog
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityDialog
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityPlace
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityType
import com.entin.lighttasks.presentation.screens.section.adapter.SectionPreferencesAdapter
import com.entin.lighttasks.presentation.screens.section.adapter.SectionTouchHelperCallback
import com.entin.lighttasks.presentation.util.ZERO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class SectionFragment : Fragment(R.layout.section_preferences) {

    private var _binding: SectionPreferencesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SectionViewModel by viewModels()
    private var sectionPreferencesAdapter: SectionPreferencesAdapter? = SectionPreferencesAdapter(
        onEdit = ::openEditSectionDialog,
        onDelete = ::openDeleteSectionDialog,
        updateDb = ::updateAllSections,
        onPasswordClick = ::onPasswordClick
    )

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
                    sectionsAdapterList = adapter
                ),
            ).attachToRecyclerView(binding.sectionPreferencesRecyclerView)
        }
    }

    private fun stateObserver() {
        viewModel.sectionEvent.observe(viewLifecycleOwner) { event: SectionsEventContract ->
            when (event) {
                is SectionsEventContract.ShowAllSections ->
                    sectionPreferencesAdapter?.submitList(event.sectionEntities)

                is SectionsEventContract.CheckPassword -> checkPassword(
                    sectionId = event.sectionId, securityItemId = event.securityItemId
                )

                is SectionsEventContract.CheckPasswordDeletion -> checkPasswordForSectionDeletion(
                    section = event.section, securityItemId = event.securityItemId
                )
            }
        }
    }

    private fun updateAllSections(listSection: List<SectionEntity>) {
        viewModel.updateSections(listSection)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun onPasswordClick(section: SectionEntity) {
        if (section.hasPassword) {
            viewModel.checkPasswordForSectionById(section.id)
        } else {
            val dialog = SecurityDialog().newInstance(
                type = SecurityType.Create(SecurityPlace.SECTION),
                onSuccess = { password ->
                    viewModel.setPassword(password, section.id)
                }
            )
            if (!dialog.isVisible) {
                dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun checkPassword(sectionId: Int, securityItemId: Int) {
        val dialog = SecurityDialog().newInstance(
            type = SecurityType.Check(SecurityPlace.SECTION),
            onSuccess = { _ ->
                viewModel.deletePassword(sectionId)
            },
            securityItemId = securityItemId
        )
        if (!dialog.isVisible) {
            dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun checkPasswordForSectionDeletion(section: SectionEntity, securityItemId: Int) {
        val dialog = SecurityDialog().newInstance(
            type = SecurityType.Check(SecurityPlace.SECTION),
            onSuccess = { _ ->
                setActualSection(section)
                viewModel.deleteSection()
            },
            securityItemId = securityItemId
        )
        if (!dialog.isVisible) {
            dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
        }
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
    private fun openEditSectionDialog(sectionEntity: SectionEntity) {
        if (!createEditSectionDialog.isVisible) {
            setActualSection(sectionEntity)
            createEditSectionDialog.show(
                childFragmentManager, CreateEditSectionDialog::class.simpleName
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun openDeleteSectionDialog(sectionEntity: SectionEntity) {
        if(sectionEntity.hasPassword) {
            viewModel.checkPasswordForSectionDeletionById(sectionEntity)
        } else {
            if (!deleteSectionDialog.isVisible) {
                setActualSection(sectionEntity)
                deleteSectionDialog.show(
                    childFragmentManager, CreateEditSectionDialog::class.simpleName
                )
            }
        }
    }

    private fun setupFabCircleButton() {
        binding.fab.setOnClickListener {
            openCreateSectionDialog()
        }
    }

    private fun setActualSection(sectionEntity: SectionEntity?) {
        viewModel.currentSectionEntity = sectionEntity
    }

    override fun onDestroyView() {
        sectionPreferencesAdapter = null
        _binding = null
        super.onDestroyView()
    }
}
