package com.example.movies.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.domain.entities.filtering.ParameterForFiltering
import com.example.movies.R
import com.example.movies.databinding.ItemForSearchSettingsDetailsBinding
import com.example.movies.presentation.utilities.DiffUtilItemCallBackForSearchSettingsDetails

class AdapterForSearchSettingsDetails :
    ListAdapter<ParameterForFiltering, SearchSettingsDetailsViewHolder>(
        DiffUtilItemCallBackForSearchSettingsDetails()
    ) {

    private var toDoOnItemClick: ((parameterForFiltering: ParameterForFiltering, view: View) -> Unit)? =
        null

    private var chosenItemId: Int? = null

    private var parent: ViewGroup? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchSettingsDetailsViewHolder {
        this.parent = parent
        val binding = ItemForSearchSettingsDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchSettingsDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchSettingsDetailsViewHolder, position: Int) {
        val item = getItem(position)
        if (item.value == "") {
            holder.binding.text.visibility = View.GONE
            holder.binding.border.visibility = View.GONE
        } else {
            holder.binding.text.text = item.value
            holder.binding.root.setOnClickListener { view ->
                toDoOnItemClick?.let { toDoOnItemClick ->
                    toDoOnItemClick(item, view)
                }
            }

            if (item.id == chosenItemId) {
                holder.binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        parent!!.context,
                        R.color.light_gray
                    )
                )
            } else {
                holder.binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        parent!!.context,
                        R.color.white
                    )
                )
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).id
    }

    fun rememberChosenItem(parameterForFiltering: ParameterForFiltering) {
        chosenItemId = parameterForFiltering.id
    }

    fun setOnItemClick(toDoOnItemClick: ((parameterForFiltering: ParameterForFiltering, View) -> Unit)) {
        this.toDoOnItemClick = toDoOnItemClick
    }

    companion object {
        private const val TAG = "MoviesAppTAG"
    }
}

class SearchSettingsDetailsViewHolder(val binding: ItemForSearchSettingsDetailsBinding) :
    ViewHolder(binding.root)

