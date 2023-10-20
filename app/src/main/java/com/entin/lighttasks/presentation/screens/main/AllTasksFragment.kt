package com.entin.lighttasks.presentation.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.databinding.AllTasksBinding
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.screens.dialogs.DeleteTaskDialog
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityDialog
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityPlace
import com.entin.lighttasks.presentation.screens.dialogs.security.SecurityType
import com.entin.lighttasks.presentation.screens.main.AddEditTaskMessage.EDIT
import com.entin.lighttasks.presentation.screens.main.AddEditTaskMessage.NEW
import com.entin.lighttasks.presentation.screens.main.adapter.AllTasksAdapter
import com.entin.lighttasks.presentation.screens.main.adapter.ItemTouchHelperCallback
import com.entin.lighttasks.presentation.screens.main.adapter.OnClickOnEmpty
import com.entin.lighttasks.presentation.screens.main.adapter.SectionAdapter
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.onSearchTextChanged
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class AllTasksFragment : Fragment(R.layout.all_tasks), OnClickOnEmpty {

    private var _binding: AllTasksBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AllTasksViewModel by viewModels()

    private val tasksAdapter: AllTasksAdapter = AllTasksAdapter(
        listener = this,
        navigateToDeleteDialog = ::openDeleteDialog,
        navigateToSortDialog = ::openSortDialog,
        openTaskDetailsDialog = ::openTaskDetailsDialog,
        updateDb = ::updateAllTasks
    )

    private var sectionAdapter: SectionAdapter? = null
    private var searchView: SearchView? = null
    private var sectionId: Int = ZERO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AllTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSectionsRecyclerView()

        setupTasksRecyclerView()

        setupTasksRecyclerViewItemTouchListener()

        setupFabCircleButton()

        tasksObserver()

        setupResultListener()

        stateObserver()

        setHasOptionsMenu(true)
    }

    private fun setupTasksRecyclerView() {
        with(binding) {
            tasksRecyclerView.apply {
                adapter = tasksAdapter
                hasFixedSize()
                setItemViewCacheSize(ZERO)
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false,
                )
            }
        }
    }
    
    private fun setupSectionsRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Create SectionAdapter with preinstalled selection
            sectionId = viewModel.flowSortingPreferences.first().sectionId
            sectionAdapter = SectionAdapter(sectionId) { section ->
                sectionSelected(section)
            }

            // Set SectionAdapter on view
            with(binding) {
                sectionsRecyclerView.apply {
                    adapter = sectionAdapter
                    hasFixedSize()
                    setItemViewCacheSize(ZERO)
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false,
                    )
                }
            }

            // Fill sections
            viewModel.sections.observe(viewLifecycleOwner) { listSections ->
                setSections(listSections)
            }
        }
    }
    
    private fun setupTasksRecyclerViewItemTouchListener() {
        ItemTouchHelper(
            ItemTouchHelperCallback(
                tasksAdapterList = tasksAdapter,
                viewModel = viewModel,
            ),
        ).attachToRecyclerView(binding.tasksRecyclerView)
    }

    private fun tasksObserver() {
        viewModel.tasks.observe(viewLifecycleOwner) { listTask ->
            showWelcome(listTask.isEmpty())
            setTasks(listTask)
        }
    }

    private fun setTasks(listTask: List<Task>) {
        tasksAdapter.submitList(listTask)
    }

    private fun setSections(listSectionEntities: List<SectionEntity>) {
        sectionAdapter?.submitList(listSectionEntities)
    }
    
    private fun setupFabCircleButton() {
        binding.fab.setOnClickListener {
            viewModel.addNewTask()
        }
    }
    
    private fun setupResultListener() {
        val args: AllTasksFragmentArgs by navArgs()
        val resultType: Int = args.event
        if(resultType != ZERO) {
            viewModel.onEditResultShow(resultType)
        }
    }
    
    private fun stateObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksEvent.collect { allTasksEvent: AllTasksEvent ->
                    when (allTasksEvent) {
                        /**
                         *  After deletion task show snackbar with undo button
                         */
                        is AllTasksEvent.ShowUndoDeleteTaskMessage -> {
                            getSnackBar(
                                resources.getString(R.string.snack_bar_message_task_del),
                                requireView(),
                            ).apply {
                                setAction(resources.getString(R.string.snack_bar_btn_undo_deleted)) {
                                    viewModel.onUndoDeleteClick(allTasksEvent.task)
                                }

                            addCallback(
                                object : Snackbar.Callback() {
                                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                                        when (event) {
                                            BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT -> {
                                                viewModel.deletePasswordOfTask(allTasksEvent.task.id)
                                            }

                                            else -> {}
                                        }
                                    }

                                    override fun onShown(snackbar: Snackbar) {
                                        super.onShown(snackbar)
                                    }
                                }
                            )
                            }.show()
                        }
                        /**
                         * After adding new or editing existing task show snackbar
                         */
                        is AllTasksEvent.ShowAddEditTaskMessage -> {
                            when (allTasksEvent.type) {
                                EDIT -> getSnackBar(
                                    resources.getString(R.string.snack_bar_message_task_edit),
                                    requireView(),
                                ).show()

                                NEW -> getSnackBar(
                                    resources.getString(R.string.snack_bar_message_task_new),
                                    requireView(),
                                ).show()
                            }
                        }
                        /**
                         * Show dialog to delete all tasks with status finished
                         */
                        is AllTasksEvent.NavToDellFinishedTasks -> findNavController().navigate(
                            AllTasksFragmentDirections.actionGlobalDeleteFinishedDialog(),
                        )
                        /**
                         * After deletion all tasks with status finished show snackbar
                         */
                        is AllTasksEvent.ShowDellFinishedTasks -> getSnackBar(
                            resources.getString(R.string.snack_bar_all_finished_tasks_cleared),
                            requireView(),
                        ).show()
                        /**
                         * Show dialog to change language of Application
                         */
                        is AllTasksEvent.NavToChangeLanguage -> findNavController().navigate(
                            AllTasksFragmentDirections.actionGlobalChangeLanguageDialog(),
                        )
                        /**
                         * After undo deletion of task show snackbar be careful
                         */
                        is AllTasksEvent.RestoreTaskWithoutPhoto -> Toast.makeText(
                            requireContext(),
                            getString(R.string.be_careful),
                            Toast.LENGTH_SHORT,
                        ).show()
                        /**
                         * Navigation to edit task screen
                         */
                        is AllTasksEvent.NavToEditTask -> findNavController().navigate(
                            AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                allTasksEvent.task,
                                resources.getString(R.string.new_edit_fragment_task_edit),
                            )
                        )
                        /**
                         * Navigation to creation task screen
                         */
                        is AllTasksEvent.NavToNewTask -> findNavController().navigate(
                            AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                null,
                                resources.getString(R.string.new_edit_fragment_task_new),
                            )
                        )
                        /**
                         * Navigation to preferences screen
                         */
                        is AllTasksEvent.NavToChangePreferences -> findNavController().navigate(
                            AllTasksFragmentDirections.actionGlobalPreferencesFragment(),
                        )
                        /**
                         * Navigation to sections screen
                         */
                        is AllTasksEvent.NavToSectionPreferences -> findNavController().navigate(
                            AllTasksFragmentDirections.actionGlobalSectionFragment(),
                        )
                        /**
                         * Navigation to calendar screen
                         */
                        is AllTasksEvent.NavToCalendar -> findNavController().navigate(
                            AllTasksFragmentDirections.actionGlobalCalendarFragment(),
                        )
                        /**
                         * Check password code by security item id
                         */
                        is AllTasksEvent.CheckPassword -> {
                            checkPasswordCode(allTasksEvent.securityItemId, allTasksEvent.task)
                        }
                    }
                }
            }
        }
    }

    // Interface implementation for Adapter
    
    override fun onTaskClick(task: Task) {
        viewModel.onTaskClick(task)
    }
    
    private fun sectionSelected(sectionEntity: SectionEntity?) {
        // TODO: Check if section has password.
        //  If yes -> Ask for password and check it, after invoke viewModel normally
        //  If no -> invoke viewModel normally
        viewModel.onSectionClick(sectionEntity?.id ?: ZERO)
    }
    
    override fun onFinishedTaskClick(task: Task, mode: Boolean) {
        viewModel.onFinishedTaskClick(task, mode)
    }

    // Delete dialog for Adapter
    
    private fun openDeleteDialog(task: Task) {
        val dialog = DeleteTaskDialog().newInstance(
            task = task,
            onDelete = ::deleteTask
        )
        if (!dialog.isVisible) {
            dialog.show(
                childFragmentManager,
                DeleteTaskDialog::class.simpleName
            )
        }
    }

    private fun deleteTask(task: Task) {
        viewModel.onTaskSwipedDelete(task)
    }

    // Delete dialog for Adapter

    private fun openSortDialog(task: Task) {
        findNavController().navigate(
            AllTasksFragmentDirections.actionGlobalSortTasksByIconDialog(
                task
            )
        )
    }

    // Open dialog for Showing details of task

    private fun openTaskDetailsDialog(task: Task) {
        if(!viewModel.isManualSorting) {
            findNavController().navigate(
                AllTasksFragmentDirections.actionGlobalTaskDetailsDialog(task)
            )
        }
    }

    private fun updateAllTasks(listTaskEntities: List<Task>) {
        viewModel.updateAllTasks(listTaskEntities)
    }
    
    // Security password code check dialog
    
    private fun checkPasswordCode(securityItemId: Int, task: Task) {
        val dialog = SecurityDialog().newInstance(
            type = SecurityType.Check(SecurityPlace.TASK),
            onSuccess = { viewModel.openTask(task) },
            securityItemId = securityItemId,
        )
        if (!dialog.isVisible) {
            dialog.show(childFragmentManager, SecurityDialog::class.simpleName)
        }
    }

    // Empty List Welcome

    private fun showWelcome(show: Boolean) {
        binding.apply {
            if (show) {
                icWelcome.visibility = View.VISIBLE
                textWelcome.visibility = View.VISIBLE
            } else {
                icWelcome.visibility = View.INVISIBLE
                textWelcome.visibility = View.INVISIBLE
            }
        }
    }

    // Menu bar

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bar_menu, menu)

        // Find search element in bar
        val searchItem = menu.findItem(R.id.action_search)

        // Get Search Field in bar
        searchView = searchItem.actionView as SearchView

        // If App's RAM was cleared take last
        val pendingQuery = viewModel.searchValue.value
        if (!pendingQuery.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView?.setQuery(pendingQuery, false)
        }

        searchView?.onSearchTextChanged {
            viewModel.searchValue.value = it
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val params = viewModel.flowSortingPreferences.first()

            menu.findItem(R.id.action_hide_finished).isChecked = params.hideFinished
            menu.findItem(R.id.action_hide_events).isChecked = params.hideEvents

            sectionId = params.sectionId
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_date -> {
                viewModel.updateSortingOrder(OrderSort.SORT_BY_DATE)
                true
            }

            R.id.action_sort_by_title -> {
                viewModel.updateSortingOrder(OrderSort.SORT_BY_TITLE)
                true
            }

            R.id.action_sort_desc_asc -> {
                viewModel.updateSortASC()
                true
            }

            R.id.action_sort_calendar -> {
                viewModel.navToCalendar()
                true
            }

            R.id.action_hide_events -> {
                item.isChecked = !item.isChecked
                viewModel.updateShowEvents(item.isChecked)
                true
            }

            R.id.action_hide_finished -> {
                item.isChecked = !item.isChecked
                viewModel.updateShowFinishedTask(item.isChecked)
                true
            }

            R.id.action_sort_by_manual -> {
                viewModel.updateSortingOrder(OrderSort.SORT_BY_MANUAL)
                true
            }

            R.id.action_delete_all_tasks -> {
                viewModel.navToDelete()
                true
            }

            R.id.action_sort_by_important -> {
                viewModel.updateSortingOrder(OrderSort.SORT_BY_IMPORTANT)
                true
            }

            R.id.action_change_language -> {
                viewModel.navToChangeLanguage()
                true
            }

            R.id.action_sections -> {
                viewModel.navToSectionPreferences()
                true
            }

            R.id.action_preferences -> {
                viewModel.navToChangePreferences()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView?.setOnQueryTextListener(null)
        binding.tasksRecyclerView.adapter = null
        _binding = null
    }
}
