package com.example.movies.presentation.adapters

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.movies.presentation.fragments.home.GalleryListItemFragment


class GalleryListFragmentStateAdapter(
    fragment: Fragment,
    private val listOfImageTypes: List<String>?,
    private val listOfImageTypesQty: List<Int>,
    private val movieId: Int
) : FragmentStateAdapter(fragment) {

    init {
        if (listOfImageTypes == null) {
            val message = "listOfImageTypes is null"
            Log.d(TAG, message)
            throw Throwable(message = message)
        }
    }

    override fun getItemCount(): Int = listOfImageTypes!!.size

    override fun createFragment(position: Int): Fragment {
        val fragment = GalleryListItemFragment()
        fragment.arguments = Bundle().apply {
            putString(ARG_IMAGE_TYPE, listOfImageTypes!![position])
            putInt(ARG_TOTAL_NUMBER_OF_IMAGES, listOfImageTypesQty[position])
            putInt(ARG_MOVIE_ID, movieId)
        }
        return fragment
    }

    companion object {
        private const val TAG = "MoviesAppTAG"
        private const val ARG_IMAGE_TYPE = "image_type"
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_TOTAL_NUMBER_OF_IMAGES = "total_number_of_images"
    }
}