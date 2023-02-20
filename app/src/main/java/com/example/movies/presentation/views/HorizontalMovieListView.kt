package com.example.movies.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneral
import com.example.movies.databinding.HorizontalMovieListLayoutBinding
import com.example.movies.presentation.adapters.AdapterForHorizontalMovieList
import com.example.movies.presentation.utilities.SpaceItemDecoration
import kotlinx.coroutines.flow.Flow


class HorizontalMovieListView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val nameText: TextView
    private val allItemsButton: TextView
    private val recyclerView: RecyclerView
    private val adapter: AdapterForHorizontalMovieList
    private var itemsList: List<MovieGeneral> = emptyList()
    private var actionOnButtonClick: ((String, List<MovieGeneral>) -> Unit)? = null
    private var actionOnItemClick: ((MovieGeneral) -> Unit)? = null
    private var resourceOfAdditionalMovieInfo: ((Int) -> Flow<MovieFullInfo>)? = null
    private var _binding: HorizontalMovieListLayoutBinding? = null
    private val binding get() = _binding!!

    init {
        _binding =
            HorizontalMovieListLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        nameText = binding.nameText
        allItemsButton = binding.allItemsButton
        recyclerView = binding.recyclerView
        setUniqueIdForRecyclerView()
        adapter = AdapterForHorizontalMovieList(
            actionOnButtonClick = { premiereList ->
                actionOnButtonClick?.let {
                    it(nameText.text.toString(), premiereList)
                }
            },
            actionOnItemClick = { premiere ->
                actionOnItemClick?.let {
                    it(premiere)
                }
            }
        )
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        recyclerView.adapter = adapter
        allItemsButton.setOnClickListener {
            actionOnButtonClick?.let {
                it(nameText.text.toString(), itemsList)
            }
        }
        val divider = SpaceItemDecoration(15, 0)
        recyclerView.addItemDecoration(divider)
    }

    fun setName(name: String) {
        nameText.text = name
    }

    fun setNewList(newList: List<MovieGeneral>) {
        adapter.setNewList(newList)
        itemsList = newList
    }

    fun setActionOnButtonClick(action: (String, List<MovieGeneral>) -> Unit) {
        actionOnButtonClick = action
    }

    fun setActionOnItemClick(action: (MovieGeneral) -> Unit) {
        actionOnItemClick = action
    }

    fun setResourceOfAdditionalMovieInfo(
        action: (Int) -> Flow<MovieFullInfo>
    ) {
        resourceOfAdditionalMovieInfo = action
        resourceOfAdditionalMovieInfo?.let { lambda ->
            adapter.setResourceOfAdditionalMovieInfo { movie ->
                lambda(movie)
            }
        }
    }

    fun hideAllItemsButton() {
        binding.allItemsButton.isVisible = false
    }

    fun enableEmptyCollectionMessage(){
        binding.emptyCollectionText.visibility = View.VISIBLE
    }

    fun disableEmptyCollectionMessage(){
        binding.emptyCollectionText.visibility = View.INVISIBLE
    }

    fun turnOnPlaceholders() {
        setNewList(List(10) {
            object : MovieGeneral() {
                override val id: Int?
                    get() = null
                override val nameRu: String?
                    get() = null
                override val nameEn: String?
                    get() = null
                override val nameOriginal: String?
                    get() = null
                override val imageSmall: String?
                    get() = null
                override val imageLarge: String?
                    get() = null
                override val genres: List<String>?
                    get() = null
                override val rating: String?
                    get() = null
            }
        })
    }

    private fun setUniqueIdForRecyclerView() {
        if (recyclerViewsIdsList.keys.contains(this.id)) {
            recyclerView.id = recyclerViewsIdsList[this.id]!!
        } else {
            val recyclerViewUniqueId = View.generateViewId()
            recyclerView.id = recyclerViewUniqueId
            recyclerViewsIdsList[this.id] = recyclerView.id
        }
    }

    companion object {
        private val recyclerViewsIdsList = mutableMapOf<Int, Int>()
    }
}