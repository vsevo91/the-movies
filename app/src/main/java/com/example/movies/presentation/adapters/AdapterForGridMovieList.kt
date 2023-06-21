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
import com.bumptech.glide.Glide
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.ItemForVerticalMovieGridListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdapterForGridMovieList(
    private var list: List<MovieGeneral>,
    private val currentLocale: String
) : RecyclerView.Adapter<ViewHolderForGridMovieList>() {

    private var actionOnItemClick: ((MovieGeneral) -> Unit)? = null
    private var resourceOfAdditionalMovieInfo: ((Int) -> Flow<MovieFullInfo?>)? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())
    private var parent: ViewGroup? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForGridMovieList {
        this.parent = parent
        val binding = ItemForVerticalMovieGridListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForGridMovieList(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForGridMovieList, position: Int) {
        val movie = list[position]
        (holder.binding).apply {
            if (movie.isWatched) {
                isWatched.visibility = View.VISIBLE
                gradient.visibility = View.VISIBLE
            } else {
                isWatched.visibility = View.INVISIBLE
                gradient.visibility = View.INVISIBLE
            }
            Glide
                .with(movieImage)
                .load(list[position].imageSmall)
                .placeholder(R.drawable.gradient_background)
                .into(movieImage)

            defineMovieName(movie, movieName)

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
                        defineColorOfRatingText(movie.rating!!, rating)
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
                Log.d(APPLICATION_TAG, "LOADED FROM CACHE")
            } else {
                resourceOfAdditionalMovieInfo?.let { movieFullInfo ->
                    scope.launch {
                        movieFullInfo(movieId).collect { movieFullInfo ->
                            if (movieFullInfo != null) {
                                Log.d(APPLICATION_TAG, "RECEIVED")
                                movieCache[movieFullInfo.id] = movieFullInfo
                                Log.d(APPLICATION_TAG, "CACHED")
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun checkTextLength(text: String, textView: TextView) {
        val firstWord = text.substringBefore(" ")
        if (firstWord.length > 15) textView.maxLines = 1
        else textView.maxLines = 2
    }

    override fun getItemCount(): Int = list.size

    fun setItemClickListener(action: (MovieGeneral) -> Unit) {
        actionOnItemClick = action
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitNewList(newList: List<MovieGeneral>) {
        this.list = newList
        this.notifyDataSetChanged()
    }

    companion object {
        private val movieCache = mutableMapOf<Int, MovieFullInfo>()
    }
}

class ViewHolderForGridMovieList(val binding: ItemForVerticalMovieGridListBinding) :
    ViewHolder(binding.root)