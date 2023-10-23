package com.entin.lighttasks.presentation.screens.main.adapter

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ItemTouchHelper Callback for AllTasksAdapter
 * has 2 actions:
 *  - onSwipe -> change order of items
 *  - onMove -> for delete item.
 *
 *  If sorting is Manual -> item can be moved UP / DOWN by user
 */

@ExperimentalCoroutinesApi
class ItemTouchHelperCallback(
    private val tasksAdapterList: AllTasksAdapter,
    private val viewModel: AllTasksViewModel,
) : ItemTouchHelper.Callback() {

    /** ON MOVE */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return tasksAdapterList.onItemMove(
            viewHolder.absoluteAdapterPosition,
            target.absoluteAdapterPosition,
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        tasksAdapterList.clearView()
    }

    /** ON SWIPED */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        tasksAdapterList.onSwiped(viewHolder, direction)
    }

    /** TURN ON / OFF moving items */
    override fun getMovementFlags(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int {
        var dragFlags = UP or DOWN
        val swipeFlags = START or END

        if (!viewModel.isManualSorting) {
            dragFlags = 0
        }

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = true
}
