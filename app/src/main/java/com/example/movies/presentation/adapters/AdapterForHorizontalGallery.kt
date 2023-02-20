package com.example.movies.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.domain.entities.gallery.GalleryImage
import com.example.movies.R
import com.example.movies.databinding.ItemForHorizontalGalleryBinding


class AdapterForHorizontalGallery(
    private val actionOnItemClick: (GalleryImage) -> Unit
) : RecyclerView.Adapter<ViewHolderForHorizontalGallery>() {

    init {
        Log.d(TAG, "AdapterForHorizontalGallery created")
    }

    private var list: List<GalleryImage> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForHorizontalGallery {
        val binding = ItemForHorizontalGalleryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForHorizontalGallery(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForHorizontalGallery, position: Int) {
        val image = list[position]
        Glide.with(holder.binding.galleryImage)
            .load(image.previewUrl)
            .placeholder(R.drawable.gradient_background)
            .centerCrop()
            .into(holder.binding.galleryImage)
        holder.binding.root.setOnClickListener {
            actionOnItemClick(image)
        }
    }

    override fun getItemCount(): Int {
        return if (list.size <= MAX_IMAGES_LIST_SIZE) list.size
        else MAX_IMAGES_LIST_SIZE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewList(newList: List<GalleryImage>) {
        list = newList
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "MoviesAppTAG"
        private const val MAX_IMAGES_LIST_SIZE = 20
    }
}

class ViewHolderForHorizontalGallery(val binding: ItemForHorizontalGalleryBinding) :
    ViewHolder(binding.root)