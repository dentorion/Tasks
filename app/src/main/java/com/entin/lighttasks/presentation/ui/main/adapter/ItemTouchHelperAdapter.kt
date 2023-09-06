package com.entin.lighttasks.presentation.ui.main.adapter

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
}
