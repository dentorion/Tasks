package com.entin.lighttasks.presentation.screens.addedit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.RecyclerRadioButtonItemBinding
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.presentation.util.getIconTaskDrawable

class IconsTaskAdapter(
    private val defaultSelectedIcon: Int,
    private val onClick: (element: IconTask, position: Int?) -> Unit,
) : ListAdapter<IconTask, IconsTaskAdapter.RadioButtonViewHolder>(
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
            )
        )

    override fun onBindViewHolder(holder: RadioButtonViewHolder, position: Int) {
        val radioItemElement = getItem(position)
        holder.bind(radioItemElement, position, holder.absoluteAdapterPosition)
    }

    inner class RadioButtonViewHolder(
        private val binding: RecyclerRadioButtonItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            icon: IconTask,
            position: Int,
            adapterPosition: Int
        ) {
            binding.apply {
                this.icon.isChecked = if (selectedPosition == RecyclerView.NO_POSITION) {
                    icon.groupId == defaultSelectedIcon
                } else {
                    position == selectedPosition
                }
                this.icon.setBackgroundResource(getIconTaskDrawable(icon.groupId))
                this.icon.setOnClickListener {
                    selectedPosition = adapterPosition
                    onClick(icon, position)
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
