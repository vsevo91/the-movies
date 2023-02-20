package com.example.movies.presentation.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.entities.movie.MovieGeneralList
import com.example.movies.presentation.fragments.home.StaffFilmographyItemFragment

class StaffFilmographyFragmentStateAdapter(
    fragment: Fragment,
    private val listOfMovie: List<List<MovieGeneral>>,
    private val pageCount: Int
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        val fragment = StaffFilmographyItemFragment()
        val movieList = listOfMovie[position]
        val movieListParcelable = MovieGeneralList(items = movieList)
        fragment.arguments = Bundle().apply {
            putParcelable(ARG_MOVIE_LIST, movieListParcelable)
        }
        return fragment
    }

    companion object {
        private const val ARG_MOVIE_LIST = "staff_movie_list"
    }
}