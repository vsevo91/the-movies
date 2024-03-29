package com.example.movies.presentation.adapters

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneral
import com.example.movies.R
import com.example.movies.databinding.ItemForVerticalMovieListInCollectionBinding
import com.example.movies.presentation.utilities.DiffUtilItemCallBackForMovies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdapterForVerticalMovieListInCollection(
    private val currentLocale: String
) :
    ListAdapter<MovieGeneral, ViewHolderForVerticalMovieListInCollection>(
        DiffUtilItemCallBackForMovies()
    ) {

    private var actionOnItemClick: ((MovieGeneral) -> Unit)? = null
    private var actionOnDeleteButtonClick: ((MovieGeneral) -> Unit)? = null
    private var resourceOfAdditionalMovieInfo: ((Int) -> Flow<MovieFullInfo?>)? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())
    private var parent: ViewGroup? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForVerticalMovieListInCollection {
        this.parent = parent
        val binding = ItemForVerticalMovieListInCollectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForVerticalMovieListInCollection(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolderForVerticalMovieListInCollection,
        position: Int
    ) {
        val movie = getItem(position)
        (holder.binding).apply {
            if (movie.isWatched) {
                isWatched.visibility = View.VISIBLE
                gradient.visibility = View.VISIBLE
            } else {
                isWatched.visibility = View.INVISIBLE
                gradient.visibility = View.INVISIBLE
            }
            if (movie.rating != null) {
                rating.text = movie.rating
                rating.isVisible = true
                defineColorOfRatingText(movie.rating!!, rating)
            }
            checkParameterAndGetFullMovieInfoIfFalse(
                movieId = movie.id!!,
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

            checkParameterAndGetFullMovieInfoIfFalse(
                movieId = movie.id!!,
                parameter = movie.genres?.firstOrNull(),
                toDoIfNonNull = { genreText.text = it },
                toDoIfNull = { genreText.text = it.genres?.firstOrNull() }
            )

            defineMovieName(movie, movieName)

            root.setOnClickListener {
                actionOnItemClick?.invoke(movie)
            }
            deleteButton.setOnClickListener {
                actionOnDeleteButtonClick?.invoke(movie)
            }
        }
    }

    private fun checkTextLength(text: String, textView: TextView) {
        val firstWord = text.substringBefore(" ")
        if (firstWord.length > 14) textView.maxLines = 1
        else textView.maxLines = 2
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

    private fun defineColorOfRatingText(rating: String, textView: TextView) {
        if (rating.contains("%")) return
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

    fun setItemClickListener(action: (MovieGeneral) -> Unit) {
        this.actionOnItemClick = action
    }

    fun setOnDeleteButtonClickListener(action: (MovieGeneral) -> Unit) {
        this.actionOnDeleteButtonClick = action
    }

    private fun <T> checkParameterAndGetFullMovieInfoIfFalse(
        movieId: Int,
        parameter: T?,
        toDoIfNonNull: (T) -> Unit,
        toDoIfNull: (MovieFullInfo) -> Unit
    ) {
        if (parameter != null) {
            toDoIfNonNull(parameter)
        } else {
            if (movieCache.keys.contains(movieId)) {
                toDoIfNull(movieCache[movieId]!!)
                Log.d(TAG, "LOADED FROM CACHE")
            } else {
                resourceOfAdditionalMovieInfo?.let { movieFullInfo ->
                    scope.launch {
                        movieFullInfo(movieId).collect { movieFullInfo ->
                            if (movieFullInfo != null) {
                                Log.d(TAG, "RECEIVED")
                                movieCache[movieFullInfo.id] = movieFullInfo
                                Log.d(TAG, "CACHED")
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

    fun setResourceOfAdditionalMovieInfoIfNeeded(
        resource: (Int) -> Flow<MovieFullInfo?>
    ) {
        resourceOfAdditionalMovieInfo = resource
    }

    companion object {
        private const val TAG = "MoviesAppTAG"
        private val movieCache = mutableMapOf<Int, MovieFullInfo>()
    }
}

class ViewHolderForVerticalMovieListInCollection(val binding: ItemForVerticalMovieListInCollectionBinding) :
    ViewHolder(binding.root)