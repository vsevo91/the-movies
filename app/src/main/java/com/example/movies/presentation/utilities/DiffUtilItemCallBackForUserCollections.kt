package com.example.movies.presentation.utilities

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entities.UserCollection

class DiffUtilItemCallBackForUserCollections : DiffUtil.ItemCallback<UserCollection>() {
    override fun areItemsTheSame(oldItem: UserCollection, newItem: UserCollection): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: UserCollection, newItem: UserCollection): Boolean {
        return (oldItem.name == newItem.name &&
                oldItem.icon == newItem.icon &&
                oldItem.movies == newItem.movies)
    }
}