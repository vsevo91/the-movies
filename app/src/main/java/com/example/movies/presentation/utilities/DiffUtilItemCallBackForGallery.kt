package com.example.movies.presentation.utilities

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entities.gallery.GalleryImage

class DiffUtilItemCallBackForGallery : DiffUtil.ItemCallback<GalleryImage>() {
    override fun areItemsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean {
        return oldItem.imageUrl == newItem.imageUrl
    }

    override fun areContentsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean {
        return oldItem == newItem
    }
}