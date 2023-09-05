package com.entin.lighttasks.presentation.ui.addedit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.RecyclerRadioButtonItemBinding
import com.entin.lighttasks.domain.entity.TaskGroup

class RadioButtonAdapter(
    private val taskGroupId: Int,
    private val onClick: (element: TaskGroup) -> Unit,
) : ListAdapter<TaskGroup, RadioButtonAdapter.RadioButtonViewHolder>(
    RadioButtonAdapterDiffCallback,
) {

    var selectedPosition = RecyclerView.NO_POSITION
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioButtonViewHolder =
        RadioButtonViewHolder(
            RecyclerRadioButtonItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun onBindViewHolder(holder: RadioButtonViewHolder, position: Int) {
        val radioItemElement = getItem(position)
        holder.bind(radioItemElement, position, holder.absoluteAdapterPosition)
    }

    inner class RadioButtonViewHolder(
        private val binding: RecyclerRadioButtonItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            radioItemElement: TaskGroup,
            position: Int,
            adapterPosition: Int,
        ) {
            binding.apply {
                radioButtonItem.isChecked = if (selectedPosition == RecyclerView.NO_POSITION) {
                    radioItemElement.groupId == taskGroupId
                } else {
                    position == selectedPosition
                }
                radioButtonItem.setBackgroundResource(radioItemElement.backgroundRes)

                this.radioButtonItem.setOnClickListener {
                    selectedPosition = adapterPosition
                    onClick(radioItemElement)
                    notifyDataSetChanged()
                }
            }
        }
    }

    companion object {
        private val RadioButtonAdapterDiffCallback =
            object : DiffUtil.ItemCallback<TaskGroup>() {
                override fun areItemsTheSame(
                    oldItem: TaskGroup,
                    newItem: TaskGroup,
                ): Boolean {
                    return oldItem.backgroundRes == newItem.backgroundRes
                }

                override fun areContentsTheSame(
                    oldItem: TaskGroup,
                    newItem: TaskGroup,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
