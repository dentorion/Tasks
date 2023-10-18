package com.entin.lighttasks.presentation.screens.dialogs.galleryImages

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.entin.lighttasks.databinding.RecyclerAttachedImageItemBinding

class GalleryImagesAdapter(
    private val onClick: (galleryImageUri: Uri) -> Unit,
) : ListAdapter<Uri, GalleryImagesAdapter.AttachedImageViewHolder>(
    GalleryImagesDiffCallback,
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachedImageViewHolder =
        AttachedImageViewHolder(
            RecyclerAttachedImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )

    override fun onBindViewHolder(holder: AttachedImageViewHolder, position: Int) {
        val attachedImageUri = getItem(position)
        holder.bind(attachedImageUri)
    }

    inner class AttachedImageViewHolder(
        private val binding: RecyclerAttachedImageItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            attachedImageUri: Uri,
        ) {
            binding.apply {
                attachedImage.load(attachedImageUri)
                attachedImageCircle.setOnClickListener {
                    onClick(attachedImageUri)
                }
            }
        }
    }

    companion object {
        private val GalleryImagesDiffCallback =
            object : DiffUtil.ItemCallback<Uri>() {
                override fun areItemsTheSame(
                    oldItem: Uri,
                    newItem: Uri,
                ): Boolean {
                    return oldItem.encodedPath == newItem.encodedPath
                }

                override fun areContentsTheSame(
                    oldItem: Uri,
                    newItem: Uri,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
