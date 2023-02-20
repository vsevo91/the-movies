package com.example.data.pagingsources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.entities.movie.MovieGeneralListPaged

class GeneralMoviesPagingSource(
    private val getMovies: suspend (Int) -> MovieGeneralListPaged
) : PagingSource<Int, MovieGeneral>() {

    override fun getRefreshKey(state: PagingState<Int, MovieGeneral>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieGeneral> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val movieTopListPaged = getMovies(page)
            val listOfImages = movieTopListPaged.films
            LoadResult.Page(
                data = listOfImages,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (page == movieTopListPaged.totalPages ||
                    movieTopListPaged.totalPages == 0
                ) null else page + 1
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE = 1
    }
}