package com.entin.lighttasks.presentation.screens.section.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.SectionItemPreferencesBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.screens.main.adapter.ItemTouchHelperAdapter
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Collections

class SectionPreferencesAdapter(
    private val onEdit: (element: Section) -> Unit,
    private val onDelete: (element: Section) -> Unit,
    private val updateDb: (List<Section>) -> Unit,
) : ListAdapter<Section, SectionPreferencesAdapter.SectionViewHolder>(RadioButtonAdapterDiffCallback),
    ItemTouchHelperAdapter {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder =
        SectionViewHolder(
            SectionItemPreferencesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
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

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Do nothing
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val newCurrentList = mutableListOf<Section>().apply { addAll(currentList) }

        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(newCurrentList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(newCurrentList, i, i - 1)
            }
        }
        submitList(newCurrentList)

        job?.cancel()
        job = scope.launch {
            delay(500L)
            newCurrentList
                .filter { it.id != ZERO }
                .mapIndexed { index, task -> task.position = index + ONE }
            updateDb(newCurrentList)
        }

        return true
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
