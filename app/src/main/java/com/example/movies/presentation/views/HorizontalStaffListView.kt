package com.example.movies.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.movies.databinding.HorizontalStaffListLayoutBinding
import com.example.movies.presentation.adapters.AdapterForHorizontalStaffList
import com.example.movies.presentation.utilities.SpaceItemDecoration


class HorizontalStaffListView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val nameText: TextView
    private val allItemsButton: TextView
    private val recyclerView: RecyclerView
    private val adapter: AdapterForHorizontalStaffList
    private var itemsList: List<StaffRelatedToMovie> = emptyList()
    private var actionOnButtonClick: ((String, List<StaffRelatedToMovie>) -> Unit)? = null
    private var actionOnItemClick: ((StaffRelatedToMovie) -> Unit)? = null
    private var _binding: HorizontalStaffListLayoutBinding? = null
    private val binding get() = _binding!!


    init {
        _binding =
            HorizontalStaffListLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        nameText = binding.nameText
        allItemsButton = binding.allItemsButton
        recyclerView = binding.recyclerView
        setUniqueIdForRecyclerView()
        val currentLocale = resources.configuration.locales[0].country
        adapter = AdapterForHorizontalStaffList(
            currentLocale,
            actionOnItemClick = { staff ->
                actionOnItemClick?.let {
                    it(staff)
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
        val divider = SpaceItemDecoration(15, 10)
        recyclerView.addItemDecoration(divider)
    }

    fun setName(name: String) {
        nameText.text = name
    }

    fun setNewList(newList: List<StaffRelatedToMovie>) {
        adapter.setNewList(newList)
        itemsList = newList
    }

    fun setActionOnButtonClick(action: (String, List<StaffRelatedToMovie>) -> Unit) {
        actionOnButtonClick = action
    }

    fun setActionOnItemClick(action: (StaffRelatedToMovie) -> Unit) {
        actionOnItemClick = action
    }

    fun setSpanCount(spanCount: Int) {
        val layoutManager = GridLayoutManager(context, spanCount)
        layoutManager.orientation = RecyclerView.HORIZONTAL
        recyclerView.layoutManager = layoutManager
    }

    fun setMaxStaffListSize(listSize: Int) = adapter.setMaxStaffListSize(listSize)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
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