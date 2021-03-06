package com.entin.lighttasks.presentation.ui.main.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentAllTasksBinding
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.ui.main.adapter.AllTasksAdapter
import com.entin.lighttasks.presentation.ui.main.adapter.ItemTouchHelperCallback
import com.entin.lighttasks.presentation.ui.main.adapter.OnClickOnEmpty
import com.entin.lighttasks.presentation.ui.main.contract.AddEditTaskMessage.*
import com.entin.lighttasks.presentation.ui.main.contract.AllTasksEvent
import com.entin.lighttasks.presentation.ui.main.viewmodel.AllTasksViewModel
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.onSearchTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AllTasksFragment : Fragment(R.layout.fragment_all_tasks), OnClickOnEmpty {

    private var _binding: FragmentAllTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllTasksViewModel by activityViewModels()
    private val tasksAdapterList: AllTasksAdapter = AllTasksAdapter(this)
    private lateinit var searchView: SearchView

    private var allTasks = mutableListOf<Task>()
    private var myJob: Job? = null
    private var firstTouchedElement: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

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
                    false
                )
            }
        }
    }

    private fun setupRecyclerItemTouchListener() {
        ItemTouchHelper(
            ItemTouchHelperCallback(
                tasksAdapterList = tasksAdapterList,
                viewModel = viewModel,
                myJob = myJob,
                firstTouchedElement = firstTouchedElement,
                context = requireContext(),
                lifecycleScope = this.lifecycleScope,
                navController = findNavController(),
                allTasks = allTasks,
                message = resources.getString(R.string.manual_order_accepted),
            )
        ).attachToRecyclerView(binding.tasksRecyclerView)
    }

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

    private fun setupFabCircleButton() {
        binding.fab.setOnClickListener {
            viewModel.addNewTask()
        }
    }

    private fun setupResultListener() {
        setFragmentResultListener("operationMode") { _, bundle ->
            viewModel.onEditResultShow(bundle.getInt("mode"))
        }
    }

    private fun stateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksEvent.collect { event ->
                    when (event) {
                        is AllTasksEvent.ShowUndoDeleteTaskMessage -> {
                            getSnackBar(
                                resources.getString(R.string.snack_bar_message_task_del),
                                requireView()
                            ).setAction(resources.getString(R.string.snack_bar_btn_undo_deleted)) {
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                        }
                        is AllTasksEvent.NavToEditTask -> {
                            val action =
                                AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                    event.task,
                                    resources.getString(R.string.new_edit_fragment_task_edit)
                                )
                            findNavController().navigate(action)
                        }
                        is AllTasksEvent.NavToNewTask -> {
                            val action =
                                AllTasksFragmentDirections.actionAllTasksFragmentToEditTaskFragment(
                                    null,
                                    resources.getString(R.string.new_edit_fragment_task_new)
                                )
                            findNavController().navigate(action)
                        }
                        is AllTasksEvent.ShowAddEditTaskMessage -> {
                            when (event.type) {
                                EDIT -> getSnackBar(
                                    resources.getString(R.string.snack_bar_message_task_edit),
                                    requireView()
                                ).show()
                                NEW -> getSnackBar(
                                    resources.getString(R.string.snack_bar_message_task_new),
                                    requireView()
                                ).show()
                                EXIST -> getSnackBar(
                                    resources.getString(R.string.snack_bar_message_task_exist),
                                    requireView()
                                ).show()
                            }
                        }
                        is AllTasksEvent.NavToDellFinishedTasks -> {
                            val action =
                                AllTasksFragmentDirections.actionGlobalDeleteFinishedDialog()
                            findNavController().navigate(action)
                        }
                        is AllTasksEvent.ShowDellFinishedTasks -> {
                            getSnackBar(
                                resources.getString(R.string.snack_bar_all_finished_tasks_cleared),
                                requireView()
                            ).show()
                        }
                        is AllTasksEvent.NavToChangeLanguage -> {
                            val action =
                                AllTasksFragmentDirections.actionGlobalChangeLanguageDialog()
                            findNavController().navigate(action)
                        }
                        is AllTasksEvent.NavToAuth -> {
                            val action =
                                AllTasksFragmentDirections.actionAllTasksFragmentToAuthFragment()
                            findNavController().navigate(action)
                        }
                        is AllTasksEvent.NavToRemoteTasks -> {
                            val action =
                                AllTasksFragmentDirections.actionAllTasksFragmentToRemoteFragment()
                            findNavController().navigate(action)
                        }
                        is AllTasksEvent.Smile -> getSnackBar(
                            getString(R.string.snack_bar_message_smile),
                            requireView()
                        ).show()
                    }
                }
            }
        }
    }

    // Interface implementation for Adapter

    override fun onTaskClick(task: Task) {
        viewModel.onTaskClick(task)
    }

    override fun onFinishedTaskClick(task: Task, mode: Boolean) {
        viewModel.onFinishedTaskClick(task, mode)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bar_menu, menu)

        // Find search element in bar
        val searchItem = menu.findItem(R.id.action_search)
        // Get Search Field in bar
        searchView = searchItem.actionView as SearchView
        // If App's RAM was cleared take last
        val pendingQuery = viewModel.searchValue.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onSearchTextChanged {
            viewModel.searchValue.value = it
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val params = viewModel.flowSortingPreferences.first()

            menu.findItem(R.id.action_sort_by_finished).isChecked = params.hideFinished

            if (Build.VERSION.SDK_INT < 26) {
                // Hide changing language item before implement old method of context updating
                menu.removeItem(R.id.action_change_language)
            }
        }
    }

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
            R.id.action_cloud_tasks -> {
                viewModel.navToCloudTasks()
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