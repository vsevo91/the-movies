package com.example.movies.presentation.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    private val horizontalSpaceHeight: Int,
    private val verticalSpaceHeight: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = horizontalSpaceHeight
        outRect.bottom = verticalSpaceHeight
        outRect.left = horizontalSpaceHeight
    }

}