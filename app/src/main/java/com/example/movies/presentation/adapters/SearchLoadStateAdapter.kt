package com.example.movies.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.databinding.SearchLoadStateLayoutBinding

class SearchLoadStateAdapter(
    private val onRetry: () -> Unit
) : LoadStateAdapter<SearchLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: SearchLoadStateViewHolder, loadState: LoadState) {
        holder.binding.prependProgress.isVisible = loadState is LoadState.Loading
        if (loadState is LoadState.Error) {
            holder.binding.retryButton.visibility = View.VISIBLE
            holder.binding.titleImage.visibility = View.VISIBLE
            holder.binding.infoText.visibility = View.VISIBLE
        } else {
            holder.binding.retryButton.visibility = View.GONE
            holder.binding.titleImage.visibility = View.GONE
            holder.binding.infoText.visibility = View.GONE
        }
        holder.binding.retryButton.setOnClickListener {
            holder.onRetry()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): SearchLoadStateViewHolder {
        val binding = SearchLoadStateLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchLoadStateViewHolder(binding, onRetry)
    }
}

class SearchLoadStateViewHolder(
    val binding: SearchLoadStateLayoutBinding,
    val onRetry: () -> Unit
) :
    RecyclerView.ViewHolder(binding.root)