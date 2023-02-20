package com.example.movies.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.domain.entities.UserCollection
import com.example.movies.databinding.ItemForUserCollectionBinding
import com.example.movies.presentation.utilities.DiffUtilItemCallBackForUserCollections

class AdapterForUserCollectionsList : ListAdapter<UserCollection, ViewHolderForUserCollectionsList>(
    DiffUtilItemCallBackForUserCollections()
) {

    private var onDeleteButtonClick: ((UserCollection) -> Unit)? = null
    private var onItemClick: ((UserCollection) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForUserCollectionsList {
        val binding =
            ItemForUserCollectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderForUserCollectionsList(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForUserCollectionsList, position: Int) {
        val collection = getItem(position)
        holder.binding.apply {
            deleteButton.isVisible = collection.canBeDeleted
            icon.setImageResource(collection.icon)
            name.text = collection.name
            counter.text = collection.movies.size.toString()
            deleteButton.setOnClickListener {
                onDeleteButtonClick?.invoke(collection)
            }
            root.setOnClickListener {
                onItemClick?.invoke(collection)
            }
        }
    }

    fun setOnDeleteButtonClick(onDeleteButtonClick: (UserCollection) -> Unit) {
        this.onDeleteButtonClick = onDeleteButtonClick
    }

    fun setOnItemClick(onItemClick: (UserCollection) -> Unit) {
        this.onItemClick = onItemClick
    }
}

class ViewHolderForUserCollectionsList(val binding: ItemForUserCollectionBinding) :
    ViewHolder(binding.root)