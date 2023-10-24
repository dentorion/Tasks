package com.entin.lighttasks.presentation.screens.dialogs.linkUrl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.entin.lighttasks.databinding.LinkItemChooseBinding
import com.entin.lighttasks.databinding.RecyclerRadioButtonItemBinding

class LinkUrlAdapter(
    private val onClick: (element: String) -> Unit,
    private val onDelete: (element: String) -> Unit,
) : ListAdapter<String, LinkUrlAdapter.LinkUrlViewHolder>(RadioButtonAdapterDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkUrlViewHolder =
        LinkUrlViewHolder(
            LinkItemChooseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )

    override fun onBindViewHolder(holder: LinkUrlViewHolder, position: Int) {
        val linkUrl: String = getItem(position)
        holder.bind(linkUrl, position, holder.absoluteAdapterPosition)
    }

    inner class LinkUrlViewHolder(
        private val binding: LinkItemChooseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            link: String,
            position: Int,
            adapterPosition: Int
        ) {
            binding.apply {
                linkUrlTitle.apply {
                    text = link
                    setOnClickListener {
                        onClick(link)
                    }
                }

                linkUrlDelete.setOnClickListener {
                    onDelete(link)
                }
            }
        }
    }

    companion object {
        private val RadioButtonAdapterDiffCallback =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
