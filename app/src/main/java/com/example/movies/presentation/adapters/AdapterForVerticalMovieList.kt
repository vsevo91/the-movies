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
import com.example.movies.R
import com.example.movies.databinding.ItemForVerticalMovieListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdapterForVerticalMovieList(
    private var list: List<MovieGeneral>
) : RecyclerView.Adapter<ViewHolderForVerticalMovieList>() {

    private var actionOnItemClick: ((MovieGeneral) -> Unit)? = null
    private var resourceOfAdditionalMovieInfo: ((Int) -> Flow<MovieFullInfo?>)? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())
    private var parent: ViewGroup? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForVerticalMovieList {
        this.parent = parent
        val binding = ItemForVerticalMovieListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForVerticalMovieList(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForVerticalMovieList, position: Int) {
        val movie = list[position]
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

            movieName.text = defineMovieName(movie, movieName)

            root.setOnClickListener {
                actionOnItemClick?.invoke(movie)
            }
        }
    }

    private fun checkTextLength(text: String, textView: TextView) {
        val firstWord = text.substringBefore(" ")
        if (firstWord.length > 14) textView.maxLines = 1
        else textView.maxLines = 2
    }

    private fun defineMovieName(movie: MovieGeneral, textView: TextView): String {
        return if (movie.nameRu != null && movie.nameRu.toString().isNotBlank()) {
            val text = movie.nameRu
            checkTextLength(text!!, textView)
            text
        } else if (movie.nameEn != null && movie.nameEn.toString().isNotBlank()) {
            val text = movie.nameEn
            checkTextLength(text!!, textView)
            text
        } else if (movie.nameOriginal != null && movie.nameOriginal.toString().isNotBlank()) {
            val text = movie.nameOriginal
            checkTextLength(text!!, textView)
            text
        } else {
            ""
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

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setItemClickListener(action: (MovieGeneral) -> Unit) {
        actionOnItemClick = action
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

    @SuppressLint("NotifyDataSetChanged")
    fun submitNewList(newList:List<MovieGeneral>){
        this.list = newList
        this.notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "MoviesAppTAG"
        private val movieCache = mutableMapOf<Int, MovieFullInfo>()
    }
}

class ViewHolderForVerticalMovieList(val binding: ItemForVerticalMovieListBinding) :
    ViewHolder(binding.root)