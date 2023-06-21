package com.example.movies.presentation.adapters

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_MOVIES_FOR_HORIZONTAL_PREVIEW
import com.example.movies.R
import com.example.movies.databinding.AllMoviesRoundButtonBinding
import com.example.movies.databinding.ItemForHorizontalMovieListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class AdapterForHorizontalMovieList(
    private val currentLocale: String,
    private val actionOnButtonClick: (List<MovieGeneral>) -> Unit,
    private val actionOnItemClick: (MovieGeneral) -> Unit
) : RecyclerView.Adapter<ViewHolderForHorizontalMovieList>() {

    private var movieListMaxSize = MAX_MOVIES_FOR_HORIZONTAL_PREVIEW
    private var list: List<MovieGeneral> = emptyList()
    private var resourceOfAdditionalMovieInfo: ((Int) -> Flow<MovieFullInfo?>)? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())
    private var parent: ViewGroup? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForHorizontalMovieList {
        this.parent = parent
        return if (viewType == movieListMaxSize) {
            val binding = AllMoviesRoundButtonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ViewHolderForHorizontalMovieList(binding)
        } else {
            val binding = ItemForHorizontalMovieListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ViewHolderForHorizontalMovieList(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderForHorizontalMovieList, position: Int) {
        val movie = list[position]
        if (movie.id == null) return
        if (holder.binding is ItemForHorizontalMovieListBinding) {
            (holder.binding).apply {
                if (movie.isWatched) {
                    isWatched.visibility = View.VISIBLE
                    gradient.visibility = View.VISIBLE
                } else {
                    isWatched.visibility = View.INVISIBLE
                    gradient.visibility = View.INVISIBLE
                }
                checkParameterAndGetFullMovieInfoIfFalse(
                    movieId = movie.id,
                    parameter = movie.rating,
                    toDoIfNonNull = {
                        if (it.isNotBlank()) {
                            rating.text = it
                            rating.isVisible = true
                            defineColorOfRatingText(it, rating)
                        } else {
                            rating.isVisible = false
                        }
                    },
                    toDoIfNull = { movieFullInfo ->
                        if (movieFullInfo.rating != null) {
                            rating.text = movieFullInfo.rating
                            rating.isVisible = true
                            defineColorOfRatingText(movieFullInfo.rating!!, rating)
                        } else if (movieFullInfo.ratingAwait != null) {
                            rating.text = "${movieFullInfo.ratingAwait} %"
                            rating.isVisible = true
                            rating.background.setTint(
                                ContextCompat.getColor(parent!!.context, R.color.violet)
                            )
                        } else {
                            rating.isVisible = false
                        }
                    }
                )
                checkParameterAndGetFullMovieInfoIfFalse(
                    movieId = movie.id,
                    parameter = movie.imageSmall,
                    toDoIfNonNull = {
                        Glide
                            .with(movieImage)
                            .load(it)
                            .centerCrop()
                            .placeholder(R.drawable.gradient_background)
                            .into(movieImage)
                    },
                    toDoIfNull = {
                        Glide
                            .with(movieImage)
                            .load(it.imageSmall)
                            .centerCrop()
                            .placeholder(R.drawable.gradient_background)
                            .into(movieImage)
                    }
                )
                defineMovieName(movie, movieName)
                checkParameterAndGetFullMovieInfoIfFalse(
                    movieId = movie.id,
                    parameter = movie.genres?.firstOrNull(),
                    toDoIfNonNull = { genreText.text = it },
                    toDoIfNull = { genreText.text = it.genres?.firstOrNull() }
                )
                root.setOnClickListener {
                    actionOnItemClick(movie)
                }
            }
        } else {
            (holder.binding as AllMoviesRoundButtonBinding).button.setOnClickListener {
                actionOnButtonClick(list)
            }
        }
    }

    private fun defineMovieName(movie: MovieGeneral, textView: TextView) {
        when {
            currentLocale == "RU" && movie.nameRu != null && movie.nameRu.toString()
                .isNotBlank() -> {
                val text = movie.nameRu!!
                checkTextLength(text, textView)
                textView.text = text
            }

            movie.nameEn != null && movie.nameEn.toString().isNotBlank() -> {
                val text = movie.nameEn!!
                checkTextLength(text, textView)
                textView.text = text
            }

            movie.nameOriginal != null && movie.nameOriginal.toString().isNotBlank() -> {
                val text = movie.nameOriginal!!
                checkTextLength(text, textView)
                textView.text = text
            }

            movie.nameRu != null && movie.nameRu.toString()
                .isNotBlank() -> {
                val text = movie.nameRu!!
                checkTextLength(text, textView)
                textView.text = text
            }
        }
    }

    override fun getItemCount(): Int {
        return if (list.size <= movieListMaxSize) list.size
        else movieListMaxSize + 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewList(newList: List<MovieGeneral>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setResourceOfAdditionalMovieInfo(
        resource: (Int) -> Flow<MovieFullInfo?>
    ) {
        resourceOfAdditionalMovieInfo = resource
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun <T> checkParameterAndGetFullMovieInfoIfFalse(
        movieId: Int?,
        parameter: T?,
        toDoIfNonNull: (T) -> Unit,
        toDoIfNull: (MovieFullInfo) -> Unit
    ) {
        if (movieId == null) return
        if (parameter != null) {
            toDoIfNonNull(parameter)
        } else {
            if (movieCache.keys.contains(movieId)) {
                toDoIfNull(movieCache[movieId]!!)
                Log.d(APPLICATION_TAG, "$movieId: LOADED FROM CACHE")
            } else {
                resourceOfAdditionalMovieInfo?.let { movieFullInfo ->
                    scope.launch {
                        movieFullInfo(movieId).collect { movieFullInfo ->
                            if (movieFullInfo != null) {
                                Log.d(APPLICATION_TAG, "${movieFullInfo.id}: RECEIVED")
                                movieCache[movieFullInfo.id] = movieFullInfo
                                Log.d(APPLICATION_TAG, "${movieFullInfo.id}: CACHED")
                                handler.post {
                                    toDoIfNull(movieFullInfo)
                                }
                            }
                            this.cancel()
                        }
                    }
                }
            }
        }
    }

    private fun defineColorOfRatingText(rating: String, textView: TextView) {
        if (rating.contains("%")) {
            textView.background.setTint(
                ContextCompat.getColor(parent!!.context, R.color.violet)
            )
            return
        }
        val ratingDouble = rating.toDouble()
        when {
            ratingDouble < 5.0 -> {
                textView.background.setTint(
                    ContextCompat.getColor(parent!!.context, R.color.red)
                )
            }
            5.0 <= ratingDouble && ratingDouble < 7.0 -> {
                textView.background.setTint(
                    ContextCompat.getColor(parent!!.context, R.color.medium_gray)
                )
            }
            ratingDouble >= 7.0 -> {
                textView.background.setTint(
                    ContextCompat.getColor(parent!!.context, R.color.green)
                )
            }
            else -> throw IllegalStateException()
        }
    }

    private fun checkTextLength(text: String, textView: TextView) {
        val firstWord = text.substringBefore(" ")
        if (firstWord.length > 14) textView.maxLines = 1
        else textView.maxLines = 2
    }

    companion object {
        private val movieCache = mutableMapOf<Int, MovieFullInfo>()
    }
}

class ViewHolderForHorizontalMovieList(val binding: ViewBinding) :
    ViewHolder(binding.root)