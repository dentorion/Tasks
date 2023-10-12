package com.entin.lighttasks.presentation.screens.addedit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.SectionItemChooseBinding
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.presentation.util.getIconTaskDrawable

class SectionChooseAdapter(
    private val onSelect: (element: SectionEntity) -> Unit,
) : ListAdapter<SectionEntity, SectionChooseAdapter.SectionViewHolder>(
    RadioButtonAdapterDiffCallback,
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder =
        SectionViewHolder(
            SectionItemChooseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val icon = getItem(position)
        holder.bind(icon, position)
    }

    inner class SectionViewHolder(
        private val binding: SectionItemChooseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            sectionEntity: SectionEntity,
            position: Int,
        ) {
            binding.apply {
                sectionChooseTitle.text = sectionEntity.title
                sectionChooseIcon.setImageResource(getIconTaskDrawable(sectionEntity.icon))
                sectionChooseImportant.isVisible = sectionEntity.isImportant
                root.setOnClickListener {
                    onSelect(sectionEntity)
                }
            }
        }
    }

    companion object {
        private val RadioButtonAdapterDiffCallback = object : DiffUtil.ItemCallback<SectionEntity>() {
            override fun areItemsTheSame(
                oldItem: SectionEntity,
                newItem: SectionEntity,
            ): Boolean {
                return oldItem.createdAt == newItem.createdAt && oldItem.editedAt == newItem.editedAt && oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: SectionEntity,
                newItem: SectionEntity,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
