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
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneral
import com.example.movies.R
import com.example.movies.databinding.ItemForVerticalMovieGridListBinding
import com.example.movies.presentation.utilities.DiffUtilItemCallBackForMovies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdapterForGridMovieListPaged(
    private val currentLocale: String
) :
    PagingDataAdapter<MovieGeneral, ViewHolderForGridMovieListPaged>(DiffUtilItemCallBackForMovies()) {

    private var actionOnItemClick: ((MovieGeneral) -> Unit)? = null
    private var resourceOfAdditionalMovieInfo: ((Int) -> Flow<MovieFullInfo?>)? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())
    private var parent: ViewGroup? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForGridMovieListPaged {
        this.parent = parent
        val binding = ItemForVerticalMovieGridListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForGridMovieListPaged(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForGridMovieListPaged, position: Int) {
        val movie = getItem(position)
        (holder.binding).apply {
            movie?.let {
                if (it.isWatched) {
                    isWatched.visibility = View.VISIBLE
                    gradient.visibility = View.VISIBLE
                } else {
                    isWatched.visibility = View.INVISIBLE
                    gradient.visibility = View.INVISIBLE
                }
            }
            Glide
                .with(movieImage)
                .load(movie?.imageSmall)
                .placeholder(R.drawable.gradient_background)
                .into(movieImage)

            defineMovieName(movie!!, movieName)

            genreText.text = movie.genres?.first()
            root.setOnClickListener {
                actionOnItemClick?.let {
                    it(movie)
                }
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

        }
    }

    fun setResourceOfAdditionalMovieInfo(
        resource: (Int) -> Flow<MovieFullInfo?>
    ) {
        resourceOfAdditionalMovieInfo = resource
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

    fun setItemClickListener(action: (MovieGeneral) -> Unit) {
        actionOnItemClick = action
    }

    companion object {
        private const val TAG = "MoviesAppTAG"
        private val movieCache = mutableMapOf<Int, MovieFullInfo>()
    }
}

class ViewHolderForGridMovieListPaged(val binding: ItemForVerticalMovieGridListBinding) :
    ViewHolder(binding.root)