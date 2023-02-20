package com.example.movies.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entities.gallery.GalleryImage
import com.example.movies.databinding.HorizontalGalleryLayoutBinding
import com.example.movies.presentation.adapters.AdapterForHorizontalGallery
import com.example.movies.presentation.utilities.SpaceItemDecoration


class HorizontalGalleryView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val nameText: TextView
    private val allItemsButton: View
    private val recyclerView: RecyclerView
    private val adapter: AdapterForHorizontalGallery
    private var itemsList: List<GalleryImage> = emptyList()
    private var actionOnButtonClick: ((String, List<GalleryImage>) -> Unit)? = null
    private var actionOnItemClick: ((GalleryImage) -> Unit)? = null
    private var _binding: HorizontalGalleryLayoutBinding? = null
    private val binding get() = _binding!!

    init {
        _binding =
            HorizontalGalleryLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        nameText = binding.nameText
        allItemsButton = binding.allItemsButton
        recyclerView = binding.recyclerView
        setUniqueIdForRecyclerView()
        adapter = AdapterForHorizontalGallery(
            actionOnItemClick = { galleryImage ->
                actionOnItemClick?.let {
                    it(galleryImage)
                }
            }
        )
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        recyclerView.adapter = adapter
        recyclerView.isNestedScrollingEnabled = false

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

    fun setNewList(newList: List<GalleryImage>) {
        adapter.setNewList(newList)
        itemsList = newList
    }

    fun turnOnPlaceholders() {
        setNewList(List(10) { GalleryImage(null, null) })
    }

    fun setNumberOfItemsAtButtonText(number: Int) {
        binding.imageNumber.text = number.toString()
    }

    fun setActionOnButtonClick(action: (String, List<GalleryImage>) -> Unit) {
        actionOnButtonClick = action
    }

    fun setActionOnItemClick(action: (GalleryImage) -> Unit) {
        actionOnItemClick = action
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