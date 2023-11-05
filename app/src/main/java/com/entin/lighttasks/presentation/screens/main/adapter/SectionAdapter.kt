package com.entin.lighttasks.presentation.screens.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SectionItemBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.util.getIconTaskDrawable

class SectionAdapter(
    private val initialSection: Int,
    private val onClick: (element: Section?) -> Unit,
) : ListAdapter<Section, SectionAdapter.SectionViewHolder>(
    RadioButtonAdapterDiffCallback,
) {
    private var selectedItem: Section? = null

    var allowChangeSelectedBackground = false

    override fun onCurrentListChanged(
        previousList: MutableList<Section>,
        currentList: MutableList<Section>
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
            section: Section,
            position: Int,
        ) {
            binding.apply {
                val context = root.context
                sectionRoot.apply {
                    if (selectedItem == section && allowChangeSelectedBackground) {
                        setBackgroundResource(R.drawable.section_selected_background)
                        allowChangeSelectedBackground = false
                    } else {
                        setBackgroundResource(R.drawable.section_background)
                    }
                    setOnClickListener { selectItem(section, position) }
                }
                sectionTitle.text = section.title
                val color = if (section.isImportant) {
                    R.color.dark_red
                } else {
                    R.color.rose
                }
                sectionTitle.setTextColor(context.getColor(color))
                sectionIcon.setImageResource(getIconTaskDrawable(section.icon))
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeBackgroundForSelectedItem(sectionId: Int?) {
        selectedItem = currentList.firstOrNull { section -> section.id == sectionId }
        allowChangeSelectedBackground = true
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectItem(value: Section, position: Int?) {
        when {
            value == selectedItem -> {
                selectedItem = null
                onClick(null)
            }

            value != selectedItem -> {
                onClick(value)
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
