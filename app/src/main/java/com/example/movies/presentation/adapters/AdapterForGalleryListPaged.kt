package com.example.movies.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.domain.entities.gallery.GalleryImage
import com.example.movies.R
import com.example.movies.databinding.ItemForGridGalleryBinding
import com.example.movies.presentation.utilities.DiffUtilItemCallBackForGallery

class AdapterForGalleryListPaged(
    private val navigateToItemFullScreenFragment: (GalleryImage) -> Unit
) : PagingDataAdapter<GalleryImage, GalleryListPagedViewHolder>(DiffUtilItemCallBackForGallery()) {

    override fun onBindViewHolder(holder: GalleryListPagedViewHolder, position: Int) {
        val previewUrl = getItem(position)?.previewUrl
        val imageUrl = getItem(position)?.imageUrl
        previewUrl?.let { urlNonNull ->
            holder.binding.apply {
                Glide
                    .with(galleryImage)
                    .load(urlNonNull)
                    .placeholder(R.drawable.gradient_background)
                    .into(galleryImage)
            }
        }
        imageUrl?.let {
            holder.binding.root.setOnClickListener { navigateToItemFullScreenFragment(getItem(position)!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryListPagedViewHolder {
        val binding = ItemForGridGalleryBinding.inflate(LayoutInflater.from(parent.context))
        return GalleryListPagedViewHolder(binding)
    }
}

class GalleryListPagedViewHolder(val binding: ItemForGridGalleryBinding) :
    ViewHolder(binding.root)