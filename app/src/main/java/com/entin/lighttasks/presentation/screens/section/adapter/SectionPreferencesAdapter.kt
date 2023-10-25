package com.entin.lighttasks.presentation.screens.section.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.R
import com.entin.lighttasks.databinding.SectionItemPreferencesBinding
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.presentation.screens.main.adapter.ItemTouchHelperAdapter
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.getIconTaskDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Collections

class SectionPreferencesAdapter(
    private val onEdit: (element: Section) -> Unit,
    private val onDelete: (element: Section) -> Unit,
    private val updateDb: (List<Section>) -> Unit,
    private val onPasswordClick: ((element: Section) -> Unit)? = null,
) : ListAdapter<Section, SectionPreferencesAdapter.SectionViewHolder>(RadioButtonAdapterDiffCallback),
    ItemTouchHelperAdapter {

    private var newCurrentList: List<Section> = mutableListOf()
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
            sectionEntity: Section,
            position: Int,
        ) {
            binding.apply {
                sectionTitle.text = sectionEntity.title
                sectionIcon.setImageResource(getIconTaskDrawable(sectionEntity.icon))
                sectionImportant.isVisible = sectionEntity.isImportant
                sectionChoosePassword.apply {
                    if (onPasswordClick == null) {
                        visibility = View.INVISIBLE
                    } else {
                        val iconForPasswordButton = if (sectionEntity.hasPassword) {
                            R.drawable.ic_unlock
                        } else {
                            R.drawable.ic_lock
                        }
                        setImageResource(iconForPasswordButton)
                        setOnClickListener {
                            onPasswordClick.let { onClick -> onClick(sectionEntity) }
                        }
                    }
                }
                sectionUpdate.setOnClickListener {
                    onEdit(sectionEntity)
                }
                sectionDelete.setOnClickListener {
                    onDelete(sectionEntity)
                }
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Do nothing
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        try {
            newCurrentList = mutableListOf<Section>().apply { addAll(currentList) }
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(newCurrentList, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(newCurrentList, i, i - 1)
                }
            }
            newCurrentList
                .filter { it.id != ZERO }
                .mapIndexed { index, task -> task.position = index + ONE }
            submitList(newCurrentList)
            return true
        } catch (e: IndexOutOfBoundsException) {
            return false
        }
    }

    fun clearView() {
        job?.cancel()
        job = scope.launch {
            updateDb(newCurrentList.toList())
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
