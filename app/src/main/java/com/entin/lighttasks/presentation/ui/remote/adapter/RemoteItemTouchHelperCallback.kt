package com.entin.lighttasks.presentation.ui.remote.adapter

import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.presentation.ui.main.fragment.AllTasksFragmentDirections

/**
 * ItemTouchHelper Callback for AllTasksAdapter
 * has 2 actions:
 *  - onSwipe -> change order of items
 *  - onMove -> for delete item.
 *
 *  If sorting is Manual -> item can be moved UP DOWN
 *                else -> item can't be moved UP DOWN
 */

class RemoteItemTouchHelperCallback(
    private val tasksAdapterList: RemoteAllTasksAdapter,
    private val navController: NavController,
) : ItemTouchHelper.SimpleCallback(0, START or END) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val task = tasksAdapterList.currentList[viewHolder.absoluteAdapterPosition]
        val action =
            AllTasksFragmentDirections.actionGlobalDeleteTaskDialog(task = task, remote = true)
        navController.navigate(action)
        // To prevent empty place of task in recyclerview
        tasksAdapterList.notifyItemChanged(viewHolder.absoluteAdapterPosition)
    }
}