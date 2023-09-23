package com.entin.lighttasks.presentation.screens.calendar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.RecyclerIconSortItemBinding
import com.entin.lighttasks.domain.entity.IconTask

class SortByIconAdapter(
    private val getSelectedIcon: () -> Int,
    private val onClick: (element: IconTask?, position: Int?) -> Unit,
) : ListAdapter<IconTask, SortByIconAdapter.RadioButtonViewHolder>(
    RadioButtonAdapterDiffCallback,
) {

    var selectedPosition = RecyclerView.NO_POSITION
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioButtonViewHolder =
        RadioButtonViewHolder(
            RecyclerIconSortItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )

    override fun onBindViewHolder(holder: RadioButtonViewHolder, position: Int) {
        val icon = getItem(position)
        holder.bind(icon, position, holder.absoluteAdapterPosition)
    }

    inner class RadioButtonViewHolder(
        private val binding: RecyclerIconSortItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            radioItemElement: IconTask,
            position: Int,
            adapterPosition: Int
        ) {
            binding.apply {
                icon.isChecked = if (selectedPosition == RecyclerView.NO_POSITION) {
                    radioItemElement.groupId == getSelectedIcon()
                } else {
                    position == selectedPosition
                }
                icon.setBackgroundResource(radioItemElement.backgroundRes)

                this.icon.setOnClickListener {
                    if(selectedPosition != adapterPosition) {
                        selectedPosition = adapterPosition
                        onClick(radioItemElement, position)
                    } else {
                        selectedPosition = RecyclerView.NO_POSITION
                        onClick(null, position)
                        icon.isChecked = false
                    }
                    notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        private val RadioButtonAdapterDiffCallback =
            object : DiffUtil.ItemCallback<IconTask>() {
                override fun areItemsTheSame(
                    oldItem: IconTask,
                    newItem: IconTask,
                ): Boolean {
                    return oldItem.backgroundRes == newItem.backgroundRes
                }

                override fun areContentsTheSame(
                    oldItem: IconTask,
                    newItem: IconTask,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
