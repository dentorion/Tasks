package com.entin.lighttasks.presentation.screens.addedit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.SectionItemChooseBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.util.getIconTaskDrawable

class SectionChooseAdapter(
    private val onSelect: (element: Section) -> Unit,
) : ListAdapter<Section, SectionChooseAdapter.SectionViewHolder>(
    SectionChooseAdapterDiffCallback,
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
            section: Section,
            position: Int,
        ) {
            binding.apply {
                sectionChooseTitle.text = section.title
                sectionChooseIcon.setImageResource(getIconTaskDrawable(section.icon))
                sectionChooseImportant.isVisible = section.isImportant
                root.setOnClickListener {
                    onSelect(section)
                }
            }
        }
    }

    companion object {
        private val SectionChooseAdapterDiffCallback = object : DiffUtil.ItemCallback<Section>() {
            override fun areItemsTheSame(
                oldItem: Section,
                newItem: Section,
            ): Boolean =
                oldItem.createdAt == newItem.createdAt &&
                        oldItem.editedAt == newItem.editedAt &&
                        oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: Section,
                newItem: Section,
            ): Boolean =
                oldItem == newItem
        }
    }
}
