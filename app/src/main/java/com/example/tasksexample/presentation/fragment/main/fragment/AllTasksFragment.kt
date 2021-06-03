package com.example.tasksexample.presentation.fragment.main.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.tasksexample.R
import com.example.tasksexample.databinding.FragmentAllTasksBinding
import com.example.tasksexample.domain.entity.OrderSort
import com.example.tasksexample.domain.entity.Task
import com.example.tasksexample.presentation.fragment.main.adapter.AllTasksAdapter
import com.example.tasksexample.presentation.fragment.main.adapter.OnClickOnEmpty
import com.example.tasksexample.presentation.fragment.main.contract.AddEditTaskMessage.EDIT
import com.example.tasksexample.presentation.fragment.main.contract.AddEditTaskMessage.NEW
import com.example.tasksexample.presentation.fragment.main.contract.AllTasksEvent
import com.example.tasksexample.presentation.fragment.main.viewmodel.AllTasksViewModel
import com.example.tasksexample.presentation.util.getSnackBar
import com.example.tasksexample.presentation.util.onSearchTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AllTasksFragment : Fragment(R.layout.fragment_all_tasks), OnClickOnEmpty {

    private val binding: FragmentAllTasksBinding by viewBinding()
    private val vm: AllTasksViewModel by activityViewModels()
    private lateinit var tasksAdapterList: AllTasksAdapter
    private lateinit var searchView: SearchView

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        setupFabCircleButton()

        populateRecyclerAdapter()

        setupResultListener()

        stateObserver()

        setHasOptionsMenu(true)
    }

    private fun setupRecyclerView() {

        tasksAdapterList = AllTasksAdapter(this)

        with(binding) {
            tasksRecyclerView.apply {
                adapter = tasksAdapterList
                hasFixedSize()
                setItemViewCacheSize(0)
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )

                ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val task = tasksAdapterList.currentList[viewHolder.absoluteAdapterPosition]
                        vm.onTaskSwiped(task)
                    }
                }).attachToRecyclerView(tasksRecyclerView)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun populateRecyclerAdapter() {
        vm.tasks.observe(viewLifecycleOwner) { listTask ->
            if (listTask.isEmpty()) {
                showWelcome(true)
            } else {
                showWelcome(false)
            }
            tasksAdapterList.submitList(listTask)
        }
    }

    private fun setupFabCircleButton() {
        binding.fab.setOnClickListener {
            vm.addNewTask()
        }
    }

    private fun setupResultListener() {
        setFragmentResultListener("operationMode") { _, bundle ->
            vm.onEditResultShow(bundle.getInt("mode"))
        }
    }

    private fun stateObserver() {
        vm.tasksEvent
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { event ->
                when (event) {
                    is AllTasksEvent.ShowUndoDeleteTaskMessage -> {
                        getSnackBar(resources.getString(R.string.message_task_del), requireView())
                            .setAction(resources.getString(R.string.undo_deleted)) {
                                vm.onUndoDeleteClick(event.task)
                            }
                            .show()
                    }

                    is AllTasksEvent.NavToEditTask -> {
                        val action =
                            AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                event.task,
                                resources.getString(R.string.task_edit)
                            )
                        findNavController().navigate(action)
                    }

                    is AllTasksEvent.NavToNewTask -> {
                        val action =
                            AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                null,
                                resources.getString(R.string.task_new)
                            )
                        findNavController().navigate(action)
                    }

                    is AllTasksEvent.ShowAddEditTaskMessage -> {
                        when (event.type) {
                            EDIT -> getSnackBar(
                                resources.getString(R.string.message_task_edit),
                                requireView()
                            ).show()
                            NEW -> getSnackBar(
                                resources.getString(R.string.message_task_new),
                                requireView()
                            ).show()
                        }
                    }

                    is AllTasksEvent.NavToDellFinishedTasks -> {
                        val action = AllTasksFragmentDirections.actionGlobalDeleteFinishedDialog()
                        findNavController().navigate(action)
                    }

                    is AllTasksEvent.ShowDellFinishedTasks -> {
                        getSnackBar(
                            resources.getString(R.string.all_finished_tasks_cleared),
                            requireView()
                        )
                            .show()
                    }

                    is AllTasksEvent.NavToChangeLanguage -> {
                        val action = AllTasksFragmentDirections.actionGlobalChangeLanguageDialog()
                        findNavController().navigate(action)
                    }
                }
            }.launchIn(lifecycleScope)
    }

    // Interface implementation for Adapter

    override fun onTaskClick(task: Task) {
        vm.onTaskClick(task)
    }

    override fun onFinishedTaskClick(task: Task, mode: Boolean) {
        vm.onFinishedTaskClick(task, mode)
    }

    // Empty List Welcome

    private fun showWelcome(show: Boolean) {
        if (show) {
            binding.apply {
                icWelcome.visibility = View.VISIBLE
                textWelcome.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                icWelcome.visibility = View.INVISIBLE
                textWelcome.visibility = View.INVISIBLE
            }
        }
    }

    // Menu bar

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bar_menu, menu)

        // Find search element in bar
        val searchItem = menu.findItem(R.id.action_search)
        // Get Search Field in bar
        searchView = searchItem.actionView as SearchView
        // If App's RAM was cleared take last
        val pendingQuery = vm.searchValue.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onSearchTextChanged {
            vm.searchValue.value = it
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val params = vm.flowSortingPreferences.first()

            menu.findItem(R.id.action_sort_by_finished).isChecked =
                params.showFinished
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_date -> {
                vm.updateSortOrder(OrderSort.SORT_BY_DATE)
                true
            }
            R.id.action_sort_by_title -> {
                vm.updateSortOrder(OrderSort.SORT_BY_TITLE)
                true
            }
            R.id.action_sort_by_finished -> {
                item.isChecked = !item.isChecked
                vm.updateFinishedOrder(item.isChecked)
                true
            }
            R.id.action_delete_all_tasks -> {
                vm.navToDelete()
                true
            }
            R.id.action_sort_by_important -> {
                vm.updateImportantOrder(OrderSort.SORT_BY_IMPORTANT)
                true
            }
            R.id.action_change_language -> {
                vm.navToChangeLanguage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        binding.tasksRecyclerView.adapter = null
    }
}