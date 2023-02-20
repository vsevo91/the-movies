package com.example.movies.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.domain.entities.UserCollection
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.ItemForUserCollectionSmallBinding
import com.example.movies.presentation.utilities.DiffUtilItemCallBackForUserCollections

class AdapterForUserCollectionsListInDialog(
    private val currentMovieId: Int
) :
    ListAdapter<UserCollection, ViewHolderForUserCollectionsListInDialog>(
        DiffUtilItemCallBackForUserCollections()
    ) {

    private var onItemClick: ((UserCollection) -> Unit)? = null
    private var onDeleteButtonClick: ((UserCollection) -> Unit)? = null
    private var onBottomButtonClick: (() -> Unit)? = null
    private var parent: ViewGroup? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderForUserCollectionsListInDialog {
        this.parent = parent
        val binding = ItemForUserCollectionSmallBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderForUserCollectionsListInDialog(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderForUserCollectionsListInDialog, position: Int) {
        if (position == itemCount - 1) {
            Log.d(APPLICATION_TAG, "Item count: $itemCount, position: $position")
            holder.binding.apply {
                collectionName.text = parent?.resources?.getString(R.string.create_collection)
                checkbox.setImageResource(R.drawable.ic_plus)
                root.setOnClickListener {
                    onBottomButtonClick?.invoke()
                }
                deleteButton.visibility = View.GONE
            }
        } else {
            Log.d(APPLICATION_TAG, "Item count: $itemCount, position: $position")
            val collection = getItem(position)
            holder.binding.apply {
                collectionName.text = collection.name
                checkbox.isSelected =
                    collection.movies.firstOrNull { it.id == currentMovieId } != null
                counter.text = collection.movies.size.toString()
                root.setOnClickListener {
                    onItemClick?.invoke(collection)
                }
                deleteButton.setOnClickListener {
                    onDeleteButtonClick?.invoke(collection)
                }
                if (!collection.canBeDeleted) {
                    deleteButton.visibility = View.INVISIBLE
                }else{
                    deleteButton.visibility = View.VISIBLE
                }
                checkbox.setImageResource(R.drawable.selector_for_check_button)
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    fun setOnItemClick(onItemClick: (UserCollection) -> Unit) {
        this.onItemClick = onItemClick
    }

    fun setOnDeleteButtonClick(onDeleteButtonClick: (UserCollection) -> Unit) {
        this.onDeleteButtonClick = onDeleteButtonClick
    }

    fun setOnBottomButtonClick(onBottomButtonClick: () -> Unit) {
        this.onBottomButtonClick = onBottomButtonClick
    }
}

class ViewHolderForUserCollectionsListInDialog(val binding: ItemForUserCollectionSmallBinding) :
    ViewHolder(binding.root)

