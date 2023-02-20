package com.example.domain.entities

import com.example.domain.entities.movie.MovieFromCollection


data class UserCollection(
    val id: Int? = null,
    val name: String,
    val icon: Int,
    val movies: List<MovieFromCollection>,
    val canBeDeleted: Boolean = true,
    val isHidden: Boolean = false
)