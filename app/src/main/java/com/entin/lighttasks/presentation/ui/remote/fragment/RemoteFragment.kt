package com.entin.lighttasks.presentation.ui.remote.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.FragmentRemoteBinding
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.ui.remote.adapter.RemoteAllTasksAdapter
import com.entin.lighttasks.presentation.ui.remote.adapter.RemoteItemTouchHelperCallback
import com.entin.lighttasks.presentation.ui.remote.contract.RemoteViewState
import com.entin.lighttasks.presentation.ui.remote.viewmodel.RemoteViewModel
import com.entin.lighttasks.presentation.util.getSnackBar
import com.entin.lighttasks.presentation.util.getUserName
import com.entin.lighttasks.presentation.util.isUserLoggedIn
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Remote fragment with list of remote saved tasks.
 * Tasks can be downloaded from Firebase to Room Db.
 * Tasks can be saved from Room Db to Firebase.
 */

@AndroidEntryPoint
class RemoteFragment : Fragment(R.layout.fragment_remote) {
    private var _binding: FragmentRemoteBinding? = null
    private val binding get() = _binding!!

    private val vm: RemoteViewModel by activityViewModels()
    private val tasksAdapterList: RemoteAllTasksAdapter =
        RemoteAllTasksAdapter(::downloadSingleTask, ::remoteTaskClick)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRemoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn()) {
            setupWelcomeMessage()
            setupButtons()
            setupRecyclerView()
            setupRecyclerItemTouchListener()
            initLoadRemoteTasks()
            observeViewState()
            initSwipeGuest()
        } else {
//            navigateToAuth()
        }
    }

    /**
     * Setup welcome message
     */
    private fun setupWelcomeMessage() {
        binding.userName.text =
            getUserName() ?: getString(R.string.remote_label_name_anonymous)
    }

    /**
     * Setup buttons reaction
     */
    private fun setupButtons() {
        binding.apply {
            // Load all remote tasks
            btnLoadAllTasks.setOnClickListener {
                vm.loadToDbAllRemoteTasks()
            }

            // Save local tasks to remote
            btnSaveLocalTasksToRemote.setOnClickListener {
                vm.saveLocalTasksToRemote()
            }

            // Log out account
            icExitAccount.setOnClickListener {
                Firebase.auth.signOut()
                navigateToMain()
            }
        }
    }

    /**
     * Setup RecyclerView
     */
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

    /**
     * Setup slide to delete
     */
    private fun setupRecyclerItemTouchListener() {
        ItemTouchHelper(
            RemoteItemTouchHelperCallback(
                tasksAdapterList = tasksAdapterList,
                navController = findNavController(),
            )
        ).attachToRecyclerView(binding.tasksRecyclerView)
    }

    /**
     * Initial load all remote tasks
     */
    private fun initLoadRemoteTasks() {
        vm.loadFromRemoteTasks(false)
    }

    /**
     * Observe View State
     */
    private fun observeViewState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.remoteTasksViewState.collect { viewState ->
                    viewStateReaction(viewState)
                }
            }
        }
    }

    /**
     * Swipe guest for refresh
     */
    private fun initSwipeGuest() {
        with(binding) {
            swipeRefresh.apply {
                setColorSchemeResources(R.color.color_main)
                setOnRefreshListener {
                    vm.loadFromRemoteTasks(isLoadShow = false)
                    isRefreshing = false
                }
            }
        }
    }

    // UTIL FUNCTIONS

    /**
     * ViewState reaction
     */
    private fun viewStateReaction(viewState: RemoteViewState) {
        when (viewState) {
            /**
             * INFORM
             */
            is RemoteViewState.Inform -> {
                // Load state
                showLoadingState(false)

                // Check what kind of information income
                when (viewState) {
                    /**
                     * INFORM. DELETE REMOTE TASK
                     */
                    is RemoteViewState.Inform.DeleteRemoteTask -> {
                        if (viewState.value) {
                            snackBar(getString(R.string.remote_del_from_firebase_success))
                        } else {
                            snackBar(getString(R.string.remote_del_from_firebase_failure))
                        }
                    }
                    /**
                     * INFORM. SAVE TO FIREBASE
                     */
                    is RemoteViewState.Inform.SaveLocalTasksToFirebase -> {
                        if (viewState.value) {
                            snackBar(getString(R.string.remote_save_to_firebase_success))
                        } else {
                            snackBar(getString(R.string.remote_save_to_firebase_failur))
                        }
                    }
                    /**
                     * INFORM. GET LIST OF REMOTE TASKS FAILURE
                     */
                    is RemoteViewState.Inform.GetListOfRemoteTasksFailure -> {
                        snackBar(getString(R.string.snack_bar_message_get_remote_tasks_failure))
                    }
                    /**
                     * INFORM. TASK SINGLE LOADED SUCCESS
                     */
                    is RemoteViewState.Inform.LoadListOfRemoteTasks -> {
                        if (viewState.value) {
                            snackBar(getString(R.string.snack_bar_message_tasks_new))
                        } else {
                            snackBar(getString(R.string.snack_bar_message_tasks_exist))
                        }
                    }
                    is RemoteViewState.Inform.LoadRemoteTask -> {
                        if (viewState.value) {
                            snackBar(getString(R.string.snack_bar_message_task_new))
                        } else {
                            snackBar(getString(R.string.snack_bar_message_task_exist))
                        }
                    }
                }
            }
            /**
             * SUCCESS
             */
            is RemoteViewState.Success -> {
                // Load state
                showLoadingState(false)
                // Fill Recyclerview
                tasksAdapterList.submitList(viewState.listRemoteTasks)
            }
            /**
             * LOADING
             */
            is RemoteViewState.Loading -> {
                // Load state
                showLoadingState(true)
            }
            else -> {
                // Load state
                showLoadingState(false)
            }
        }
    }

    /**
     * Loading state:
     * - ProgressBar
     * - Buttons
     */
    private fun showLoadingState(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading

            btnLoadAllTasks.apply {
                isEnabled = loading.not()
                alpha = if (loading) 0.5F else 1F
            }
            btnSaveLocalTasksToRemote.apply {
                isEnabled = loading.not()
                alpha = if (loading) 0.5F else 1F
            }
        }
    }

    /**
     * Add Single Task from Remote to Room Db with exist checking
     * For RecyclerView
     */
    private fun downloadSingleTask(task: Task) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.loadToDbSingleRemoteTask(task = task)
            }
        }
    }

    /**
     * Short version of snackBar invoke
     */
    private fun snackBar(value: String) {
        getSnackBar(
            value,
            requireView()
        ).show()
    }

    /**
     * Navigate to Main page
     */
    private fun navigateToMain() {
        findNavController().navigate(RemoteFragmentDirections.actionRemoteFragmentToAllTasksFragment())
    }

    /**
     * Alert Dialog with message of Task
     */
    private fun remoteTaskClick(task: Task) {
        AlertDialog.Builder(requireContext())
            .setTitle(task.title)
            .setMessage(task.message)
            .setPositiveButton("OK") { dialog, _ -> dialog?.cancel() }
            .setIcon(R.drawable.auth_main)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        vm.clearViewState()
    }
}