package com.entin.lighttasks.presentation.ui.main

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
import com.entin.lighttasks.databinding.FragmentAllTasksBinding
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.ui.main.AddEditTaskMessage.EDIT
import com.entin.lighttasks.presentation.ui.main.AddEditTaskMessage.NEW
import com.entin.lighttasks.presentation.ui.main.adapter.AllTasksAdapter
import com.entin.lighttasks.presentation.ui.main.adapter.ItemTouchHelperCallback
import com.entin.lighttasks.presentation.ui.main.adapter.OnClickOnEmpty
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.onSearchTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AllTasksFragment : Fragment(R.layout.fragment_all_tasks), OnClickOnEmpty {

    private var _binding: FragmentAllTasksBinding? = null
    private val binding get() = _binding!!

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: AllTasksViewModel by activityViewModels()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val tasksAdapterList: AllTasksAdapter = AllTasksAdapter(
        listener = this,
        navigateToDeleteDialog = ::openDeleteDialog,
        navigateToSortDialog = ::openSortDialog,
    ) { listTasks ->
        viewModel.updateAllTasks(listTasks)
    }
    private lateinit var searchView: SearchView

    private var allTasks = mutableListOf<Task>()
    private var myJob: Job? = null
    private var firstTouchedElement: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAllTasksBinding.inflate(inflater, container, false)
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

                    is AllTasksEvent.NavToEditTask -> {
                        val action =
                            AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                event.task,
                                resources.getString(R.string.new_edit_fragment_task_edit),
                            )
                        findNavController().navigate(action)
                    }

                    is AllTasksEvent.NavToNewTask -> {
                        val action =
                            AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                null,
                                resources.getString(R.string.new_edit_fragment_task_new),
                            )
                        findNavController().navigate(action)
                    }

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

                    is AllTasksEvent.Smile -> Toast.makeText(
                        requireContext(),
                        getString(R.string.be_careful),
                        Toast.LENGTH_SHORT,
                    ).show()

                    AllTasksEvent.NavToChangePreferences -> findNavController().navigate(
                        AllTasksFragmentDirections.actionGlobalPreferencesFragment(),
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
    override fun onFinishedTaskClick(task: Task, mode: Boolean) {
        viewModel.onFinishedTaskClick(task, mode)
    }

    // Delete dialog for Adapter

    private fun openDeleteDialog(task: Task) {
        findNavController().navigate(AllTasksFragmentDirections.actionGlobalDeleteTask(task))
    }

    // Delete dialog for Adapter

    private fun openSortDialog(task: Task) {
        findNavController().navigate(AllTasksFragmentDirections.actionGlobalSortTasksByIconDialog(task))
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
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onSearchTextChanged {
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

            R.id.action_sort_by_finished -> {
                item.isChecked = !item.isChecked
                viewModel.updateFinishedOrder(item.isChecked)
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

            R.id.action_preferences -> {
                viewModel.navToChangePreferences()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        binding.tasksRecyclerView.adapter = null
        _binding = null
    }
}
