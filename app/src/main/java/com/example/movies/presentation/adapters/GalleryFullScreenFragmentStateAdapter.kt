package com.example.movies.presentation.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.NETWORK_PAGE_SIZE
import com.example.movies.presentation.fragments.home.GalleryImageFullScreenItemFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class GalleryFullScreenFragmentStateAdapter(
    fragment: Fragment,
    private var currentList: List<String>,
    private val totalNumberOfImages: Int,
    private val downloadNextPageData: (page: Int) -> Flow<List<GalleryImage>>?
) : FragmentStateAdapter(fragment) {

    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Default)
    private var lastPosition: Int = 0

    init {
        Log.d(APPLICATION_TAG, "Current list size at the very beginning: ${currentList.size}")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewList(newList: List<String>) {
        currentList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = currentList.size

    override fun createFragment(position: Int): Fragment {
        lastPosition = position
        val fragment = GalleryImageFullScreenItemFragment()
        downloadMoreItems(position)
        fragment.arguments = Bundle().apply {
            putInt(ARG_CURRENT_PAGE, position + 1)
            putString(ARG_IMAGE_URL, currentList[position])
        }
        return fragment
    }

    private fun downloadMoreItems(position: Int) {
        if (currentList.size != totalNumberOfImages && position > currentList.size - 5) {
            Log.d(APPLICATION_TAG, "Loading more data")
            val nextPage = currentList.size / NETWORK_PAGE_SIZE + 1
            scope.launch {
                downloadNextPageData(nextPage)?.collect { galleryImageList ->
                    val urlList = galleryImageList.map { it.imageUrl ?: ""}
                    handler.post {
                        addNextPageOfImageListPaged(urlList)
                        this.cancel()
                    }
                }
            }
        }
    }

    fun reconnect(){
        downloadMoreItems(lastPosition)
    }

    private fun addNextPageOfImageListPaged(additionalList: List<String>) {
        val newList = currentList + additionalList
        Log.d(APPLICATION_TAG, "Additional list was added to the main")
        setNewList(newList)
        Log.d(APPLICATION_TAG, "Current list size: ${currentList.size}")
    }

    fun getItemList(): List<String> {
        return currentList
    }

    companion object {
        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_CURRENT_PAGE = "current_page"
    }
}