package com.example.movies.presentation.utilities

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entities.filtering.ParameterForFiltering

class DiffUtilItemCallBackForSearchSettingsDetails :
    DiffUtil.ItemCallback<ParameterForFiltering>() {
    override fun areItemsTheSame(
        oldItem: ParameterForFiltering,
        newItem: ParameterForFiltering
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ParameterForFiltering,
        newItem: ParameterForFiltering
    ): Boolean {
        return oldItem.id == newItem.id
    }
}