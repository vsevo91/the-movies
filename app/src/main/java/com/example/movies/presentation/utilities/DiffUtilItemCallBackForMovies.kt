package com.example.movies.presentation.utilities

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entities.movie.MovieGeneral

class DiffUtilItemCallBackForMovies : DiffUtil.ItemCallback<MovieGeneral>() {
    override fun areItemsTheSame(oldItem: MovieGeneral, newItem: MovieGeneral): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieGeneral, newItem: MovieGeneral): Boolean {
        return (oldItem.genres == newItem.genres &&
                oldItem.nameRu == newItem.nameRu &&
                oldItem.nameEn == newItem.nameEn &&
                oldItem.nameOriginal == newItem.nameOriginal &&
                oldItem.imageSmall == newItem.imageSmall &&
                oldItem.imageLarge == newItem.imageLarge &&
                oldItem.rating == newItem.rating)
    }
}