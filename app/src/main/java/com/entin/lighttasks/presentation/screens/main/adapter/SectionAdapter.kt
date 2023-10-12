package com.entin.lighttasks.presentation.screens.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SectionItemBinding
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.presentation.util.getIconTaskDrawable

class SectionAdapter(
    private val initialSection: Int,
    private val onClick: (element: SectionEntity?) -> Unit,
) : ListAdapter<SectionEntity, SectionAdapter.SectionViewHolder>(
    RadioButtonAdapterDiffCallback,
) {
    var selectedItem: SectionEntity? = null
        private set

    override fun onCurrentListChanged(
        previousList: MutableList<SectionEntity>,
        currentList: MutableList<SectionEntity>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        if(selectedItem == null) {
            selectedItem = currentList.firstOrNull { section -> section.id == initialSection }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder =
        SectionViewHolder(
            SectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
        )

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val icon = getItem(position)
        holder.bind(icon, position)
    }

    inner class SectionViewHolder(
        private val binding: SectionItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            sectionEntity: SectionEntity,
            position: Int,
        ) {
            binding.apply {
                val context = root.context
                sectionRoot.apply {
                    if (selectedItem == sectionEntity) {
                        setBackgroundResource(R.drawable.section_selected_background)
                    } else {
                        setBackgroundResource(R.drawable.section_background)
                    }
                    setOnClickListener { selectItem(sectionEntity, position) }
                }
                sectionTitle.text = sectionEntity.title
                val color = if (sectionEntity.isImportant) {
                    R.color.dark_red
                } else {
                    R.color.rose
                }
                sectionTitle.setTextColor(context.getColor(color))
                sectionIcon.setImageResource(getIconTaskDrawable(sectionEntity.icon))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectItem(value: SectionEntity, position: Int?) {
        when {
            value == selectedItem -> {
                selectedItem = null
                onClick(null)
            }

            value != selectedItem -> {
                onClick(value)
                selectedItem = value
            }
        }
        notifyDataSetChanged()
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
