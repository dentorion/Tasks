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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.AllTasksBinding
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.screens.main.AddEditTaskMessage.EDIT
import com.entin.lighttasks.presentation.screens.main.AddEditTaskMessage.NEW
import com.entin.lighttasks.presentation.screens.main.adapter.AllTasksAdapter
import com.entin.lighttasks.presentation.screens.main.adapter.ItemTouchHelperCallback
import com.entin.lighttasks.presentation.screens.main.adapter.OnClickOnEmpty
import com.entin.lighttasks.presentation.screens.main.adapter.SectionAdapter
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.onSearchTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AllTasksFragment : Fragment(R.layout.all_tasks), OnClickOnEmpty {

    private var _binding: AllTasksBinding? = null
    private val binding get() = _binding!!

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: AllTasksViewModel by activityViewModels()

    private val tasksAdapterList: AllTasksAdapter = AllTasksAdapter(
        listener = this,
        navigateToDeleteDialog = ::openDeleteDialog,
        navigateToSortDialog = ::openSortDialog,
        openTaskDetailsDialog = ::openTaskDetailsDialog,
        updateDb = ::updateAllTasks
    )

    private val sectionAdapter: SectionAdapter = SectionAdapter { section ->
        sectionSelected(section)
    }

    private var searchView: SearchView? = null

    private var allTasks = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AllTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        setupRecyclerItemTouchListener()

        setupFabCircleButton()

        allTasksObserver()

        setupResultListener()

        stateObserver()

        setHasOptionsMenu(true)
    }

    private fun setupRecyclerView() {
        with(binding) {
            tasksRecyclerView.apply {
                adapter = tasksAdapterList
                hasFixedSize()
                setItemViewCacheSize(0)
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false,
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupRecyclerItemTouchListener() {
        ItemTouchHelper(
            ItemTouchHelperCallback(
                tasksAdapterList = tasksAdapterList,
                viewModel = viewModel,
            ),
        ).attachToRecyclerView(binding.tasksRecyclerView)
    }

    @ExperimentalCoroutinesApi
    private fun allTasksObserver() {
        viewModel.tasks.observe(viewLifecycleOwner) { listTask ->
            showWelcome(listTask.isEmpty())
            allTasks.apply {
                clear()
                addAll(listTask)
            }
            setTasks(listTask)
        }
    }

    private fun setTasks(listTask: List<Task>) {
        tasksAdapterList.submitList(listTask)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupFabCircleButton() {
        binding.fab.setOnClickListener {
            viewModel.addNewTask()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun setupResultListener() {
        setFragmentResultListener("operationMode") { _, bundle ->
            viewModel.onEditResultShow(bundle.getInt("mode"))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun stateObserver() {
        viewModel.tasksEvent
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { event: AllTasksEvent ->
                when (event) {
                    is AllTasksEvent.ShowUndoDeleteTaskMessage -> getSnackBar(
                        resources.getString(R.string.snack_bar_message_task_del),
                        requireView(),
                    ).setAction(resources.getString(R.string.snack_bar_btn_undo_deleted)) {
                        viewModel.onUndoDeleteClick(event.task)
                    }.show()

                    is AllTasksEvent.NavToEditTask -> findNavController().navigate(
                        AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                            event.task,
                            resources.getString(R.string.new_edit_fragment_task_edit),
                        )
                    )

                    is AllTasksEvent.NavToNewTask -> findNavController().navigate(
                        AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                            null,
                            resources.getString(R.string.new_edit_fragment_task_new),
                        )
                    )

                    is AllTasksEvent.ShowAddEditTaskMessage -> {
                        when (event.type) {
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

                    is AllTasksEvent.NavToDellFinishedTasks -> findNavController().navigate(
                        AllTasksFragmentDirections.actionGlobalDeleteFinishedDialog(),
                    )

                    is AllTasksEvent.ShowDellFinishedTasks -> getSnackBar(
                        resources.getString(R.string.snack_bar_all_finished_tasks_cleared),
                        requireView(),
                    ).show()

                    is AllTasksEvent.NavToChangeLanguage -> findNavController().navigate(
                        AllTasksFragmentDirections.actionGlobalChangeLanguageDialog(),
                    )

                    is AllTasksEvent.RestoreTaskWithoutPhoto -> Toast.makeText(
                        requireContext(),
                        getString(R.string.be_careful),
                        Toast.LENGTH_SHORT,
                    ).show()

                    is AllTasksEvent.NavToChangePreferences -> findNavController().navigate(
                        AllTasksFragmentDirections.actionGlobalPreferencesFragment(),
                    )

                    is AllTasksEvent.NavToSectionPreferences -> findNavController().navigate(
                        AllTasksFragmentDirections.actionGlobalSectionFragment(),
                    )

                    is AllTasksEvent.NavToCalendar -> findNavController().navigate(
                        AllTasksFragmentDirections.actionGlobalCalendarFragment(),
                    )
                }
            }.launchIn(lifecycleScope)
    }

    // Interface implementation for Adapter

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onTaskClick(task: Task) {
        viewModel.onTaskClick(task)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun sectionSelected(section: Section?) {
        viewModel.onSectionClick(section?.id ?: ZERO)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onFinishedTaskClick(task: Task, mode: Boolean) {
        viewModel.onFinishedTaskClick(task, mode)
    }

    // Delete dialog for Adapter

    private fun openDeleteDialog(task: Task) {
        findNavController().navigate(AllTasksFragmentDirections.actionGlobalDeleteTask(task))
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun openTaskDetailsDialog(task: Task) {
        if(!viewModel.isManualSorting) {
            findNavController().navigate(
                AllTasksFragmentDirections.actionGlobalTaskDetailsDialog(task)
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun updateAllTasks(listTasks: List<Task>) {
        viewModel.updateAllTasks(listTasks)
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

    @OptIn(ExperimentalCoroutinesApi::class)
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

            menu.findItem(R.id.action_sort_by_finished).isChecked = params.hideFinished
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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

            R.id.action_sort_by_finished -> {
                item.isChecked = !item.isChecked
                viewModel.updateShowFinishedTask(item.isChecked)
                true
            }

            R.id.action_sort_calendar -> {
                viewModel.navToCalendar()
                true
            }

            R.id.action_hide_date_pick -> {
                item.isChecked = !item.isChecked
                viewModel.updateShowDatePickedTask(item.isChecked)
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
