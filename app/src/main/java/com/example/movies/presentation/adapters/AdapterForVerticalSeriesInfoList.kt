package com.example.movies.presentation.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.domain.entities.series.Episode
import com.example.movies.R
import com.example.movies.databinding.ItemForSeriesInfoBinding

class AdapterForVerticalSeriesList(
    private var list: List<Episode>,
    private val resources: Resources
) : RecyclerView.Adapter<ViewHolderForVerticalSeriesList>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForVerticalSeriesList {

        val binding = ItemForSeriesInfoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForVerticalSeriesList(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForVerticalSeriesList, position: Int) {
        val episode = list[position]
        holder.binding.apply {
            numberName.text =
                resources.getString(R.string.episode_n, position + 1, defineName(episode))
            date.text = episode.releaseDate ?: resources.getString(R.string.unknown_general)
        }
    }

    private fun defineName(episode: Episode): String {
        return if (episode.nameRu != null &&
            episode.nameRu.toString().isNotBlank()
        ) {
            episode.nameRu!!
        } else {
            episode.nameEn ?: ""
        }
    }

    override fun getItemCount(): Int = list.size
}

class ViewHolderForVerticalSeriesList(val binding: ItemForSeriesInfoBinding) :
    ViewHolder(binding.root)