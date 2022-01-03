package com.entin.lighttasks.presentation.ui.main.adapter

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.presentation.ui.main.fragment.AllTasksFragmentDirections
import com.entin.lighttasks.presentation.ui.main.viewmodel.AllTasksViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * ItemTouchHelper Callback for AllTasksAdapter
 * has 2 actions:
 *  - onSwipe -> change order of items
 *  - onMove -> for delete item.
 *
 *  If sorting is Manual -> item can be moved UP DOWN
 *                else -> item can't be moved UP DOWN
 */

class ItemTouchHelperCallback(
    private val tasksAdapterList: AllTasksAdapter,
    private val viewModel: AllTasksViewModel,
    private var myJob: Job?,
    private var firstTouchedElement: Task?,
    private val context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val navController: NavController,
    private val allTasks: MutableList<Task>,
    private val message: String,
) : ItemTouchHelper.SimpleCallback(UP or DOWN, START or END) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        onMoveImpl(viewHolder, target)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwipedImpl(viewHolder)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        var dragFlags = UP or DOWN
        val swipeFlags = START or END

        if (!viewModel.isManualSorting) {
            dragFlags = 0
        }

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    private fun onSwipedImpl(viewHolder: RecyclerView.ViewHolder) {
        val task = tasksAdapterList.currentList[viewHolder.absoluteAdapterPosition]
        val action =
            AllTasksFragmentDirections.actionGlobalDeleteTaskDialog(task = task, remote = false)
        navController.navigate(action)
        // To prevent empty place of task in recyclerview
        tasksAdapterList.notifyItemChanged(viewHolder.absoluteAdapterPosition)
    }

    private fun onMoveImpl(
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) {
        // Positions
        val fromPosition = viewHolder.absoluteAdapterPosition
        val toPosition = target.absoluteAdapterPosition

        // Tasks
        val taskFrom = tasksAdapterList.currentList[fromPosition]
        val taskTo = tasksAdapterList.currentList[toPosition]

        // Save movable task
        if (firstTouchedElement == null) {
            firstTouchedElement = taskFrom
        }

        // Notify changes
        tasksAdapterList.notifyItemMoved(fromPosition, toPosition)

        // Run changes after moving finished
        myJob?.cancel()
        myJob = lifecycleScope.launchWhenCreated {
            /**
             * Wait for swipe finished + 500 ms to be sure
             */
            delay(500)

            /**
             * From up to down
             * If position (field) of first touched item is less then position of target
             * - increment all positions in list of items where position > target position +1
             * - find first touched item in list and set it's position = target position +1
             *
             * From down to up
             * If position (field) of first touched item is bigger then position of target
             * - increment all positions in list of items where position > target position +1
             * - find first touched item in list and set it's position = target position
             * - increment by 1 target position of item
             */
            firstTouchedElement?.let { touched ->
                if (touched.position < taskTo.position) {
                    incrementPositions(taskTo)
                    allTasks.find { task -> task.position == touched.position }
                        .also { task -> task?.position = taskTo.position + 1 }
                } else {
                    incrementPositions(taskTo)
                    allTasks.find { task -> task.position == touched.position }
                        .also { task ->
                            task?.position = taskTo.position
                            taskTo.position += 1
                        }
                }
                /**
                 * Insert into Adapter new List<Task> not to repeat animation
                 * of changing after manual sorting.
                 * No changes will be found - no animation to show.
                 */
                tasksAdapterList.submitList(allTasks)
            }

            // Update Db list of tasks with new order
            viewModel.updateAllTasks(allTasks)

            firstTouchedElement = null
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

    private fun incrementPositions(taskTo: Task) {
        allTasks.forEach { task ->
            if (task.position >= taskTo.position + 1) {
                task.position += 1
            }
        }
    }
}