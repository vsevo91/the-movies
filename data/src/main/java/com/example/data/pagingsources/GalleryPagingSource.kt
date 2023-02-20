package com.example.data.pagingsources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.entities.gallery.GalleryImageListPaged
import com.example.domain.utilities.APPLICATION_TAG

class GalleryPagingSource(
    private val getImages: suspend (Int) -> GalleryImageListPaged
) : PagingSource<Int, GalleryImage>() {

    override fun getRefreshKey(state: PagingState<Int, GalleryImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImage> {
        val page = params.key ?: FIRST_PAGE
        Log.d(APPLICATION_TAG, "Loading page: $page")
        return try {
            val galleryImageListPaged = getImages(page)
            if (galleryImageListPaged.total == -1) {
                Log.d(APPLICATION_TAG, ONLY_FIRST_400_STATE)
                throw Throwable(ONLY_FIRST_400_STATE)
            }
            val listOfImages = galleryImageListPaged.items
            LoadResult.Page(
                data = listOfImages,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (page == galleryImageListPaged.totalPages) null else page + 1
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val ONLY_FIRST_400_STATE = "only_first_400_images_state"
    }
}