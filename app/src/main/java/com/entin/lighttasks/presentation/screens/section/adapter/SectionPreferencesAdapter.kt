package com.entin.lighttasks.presentation.screens.section.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.SectionItemPreferencesBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.util.getIconTaskDrawable

class SectionPreferencesAdapter(
    private val onEdit: (element: Section) -> Unit,
    private val onDelete: (element: Section) -> Unit,
) : ListAdapter<Section, SectionPreferencesAdapter.SectionViewHolder>(
    RadioButtonAdapterDiffCallback,
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder =
        SectionViewHolder(
            SectionItemPreferencesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val icon = getItem(position)
        holder.bind(icon, position)
    }

    inner class SectionViewHolder(
        private val binding: SectionItemPreferencesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            section: Section,
            position: Int,
        ) {
            binding.apply {
                sectionTitle.text = section.title
                sectionIcon.setImageResource(getIconTaskDrawable(section.icon))
                sectionImportant.isVisible = section.isImportant
                sectionUpdate.setOnClickListener {
                    onEdit(section)
                }
                sectionDelete.setOnClickListener {
                    onDelete(section)
                }
            }
        }
    }

    companion object {
        private val RadioButtonAdapterDiffCallback = object : DiffUtil.ItemCallback<Section>() {
            override fun areItemsTheSame(
                oldItem: Section,
                newItem: Section,
            ): Boolean {
                return oldItem.createdAt == newItem.createdAt && oldItem.editedAt == newItem.editedAt && oldItem.title == newItem.title
            }

            override fun areContentsTheSame(
                oldItem: Section,
                newItem: Section,
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
