package com.entin.lighttasks.presentation.screens.addedit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.RecyclerRadioButtonItemBinding
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.presentation.util.getIconTaskDrawable

class IconsTaskAdapter(
    private val defaultSelectedIcon: Int,
    private val onClick: (element: IconTaskEntity, position: Int?) -> Unit,
) : ListAdapter<IconTaskEntity, IconsTaskAdapter.RadioButtonViewHolder>(
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
            icon: IconTaskEntity,
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
            object : DiffUtil.ItemCallback<IconTaskEntity>() {
                override fun areItemsTheSame(
                    oldItem: IconTaskEntity,
                    newItem: IconTaskEntity,
                ): Boolean {
                    return oldItem.backgroundRes == newItem.backgroundRes
                }

                override fun areContentsTheSame(
                    oldItem: IconTaskEntity,
                    newItem: IconTaskEntity,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
