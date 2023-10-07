package com.entin.lighttasks.presentation.screens.section.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.presentation.screens.main.AllTasksViewModel
import com.entin.lighttasks.presentation.screens.main.adapter.SectionAdapter
import com.entin.lighttasks.presentation.screens.section.SectionViewModel
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
class SectionTouchHelperCallback(
    private val sectionsAdapterList: SectionPreferencesAdapter,
    private val viewModel: SectionViewModel,
) : ItemTouchHelper.SimpleCallback(UP or DOWN, START or END) {

    /** ON MOVE */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return sectionsAdapterList.onItemMove(
            viewHolder.absoluteAdapterPosition,
            target.absoluteAdapterPosition,
        )
    }

    /** ON SWIPED */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    /** TURN ON / OFF moving items */
    override fun getMovementFlags(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int {
        val dragFlags = UP or DOWN

        return makeMovementFlags(dragFlags, 0)
    }

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = false
}
