package com.hlc.lib_base.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 通用 RecyclerView ItemDecoration
 * 支持 LinearLayoutManager、GridLayoutManager、StaggeredGridLayoutManager
 */
class SpaceItemDecoration private constructor(private val builder: Builder) :
    RecyclerView.ItemDecoration() {

    private val divider: Drawable? = builder.divider
    private val dividerSize: Int = builder.dividerSize
    private val horizontalSpacing: Int = builder.horizontalSpacing
    private val verticalSpacing: Int = builder.verticalSpacing
    private val edgeSpacing: Int = builder.edgeSpacing
    private val includeEdge: Boolean = builder.includeEdge
    private val showFirstDivider: Boolean = builder.showFirstDivider
    private val showLastDivider: Boolean = builder.showLastDivider

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager ?: return
        val position = parent.getChildAdapterPosition(view)
        val itemCount = parent.adapter?.itemCount ?: 0
        if (position == RecyclerView.NO_POSITION || itemCount == 0) return

        when (layoutManager) {
            is GridLayoutManager -> applyGridOffsets(outRect, position, itemCount, layoutManager)
            is StaggeredGridLayoutManager -> applyStaggeredOffsets(
                outRect,
                view,
                position,
                itemCount,
                layoutManager
            )
            is LinearLayoutManager -> applyLinearOffsets(outRect, position, itemCount, layoutManager)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (divider == null) return

        when (parent.layoutManager) {
            is GridLayoutManager -> drawGridDivider(canvas, parent)
            is StaggeredGridLayoutManager -> drawStaggeredDivider(canvas, parent)
            is LinearLayoutManager -> drawLinearDivider(canvas, parent)
        }
    }

    private fun applyLinearOffsets(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        layoutManager: LinearLayoutManager
    ) {
        if (divider != null) {
            applyLinearDividerOffsets(outRect, position, itemCount, layoutManager)
            return
        }

        val isFirst = position == 0
        val isLast = position == itemCount - 1
        val edge = if (includeEdge) edgeSpacing else 0

        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            outRect.left = edge
            outRect.right = edge
            outRect.top = if (includeEdge && isFirst) edgeSpacing else 0
            outRect.bottom = when {
                includeEdge && isLast -> edgeSpacing
                !isLast -> verticalSpacing
                else -> 0
            }
        } else {
            outRect.top = edge
            outRect.bottom = edge
            outRect.left = if (includeEdge && isFirst) edgeSpacing else 0
            outRect.right = when {
                includeEdge && isLast -> edgeSpacing
                !isLast -> horizontalSpacing
                else -> 0
            }
        }
    }

    private fun applyLinearDividerOffsets(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        layoutManager: LinearLayoutManager
    ) {
        val isFirst = position == 0
        val isLast = position == itemCount - 1

        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            outRect.top = if (isFirst && showFirstDivider) dividerSize else 0
            outRect.bottom = when {
                isLast && showLastDivider -> dividerSize
                !isLast -> dividerSize
                else -> 0
            }
        } else {
            outRect.left = if (isFirst && showFirstDivider) dividerSize else 0
            outRect.right = when {
                isLast && showLastDivider -> dividerSize
                !isLast -> dividerSize
                else -> 0
            }
        }
    }

    private fun applyGridOffsets(
        outRect: Rect,
        position: Int,
        itemCount: Int,
        layoutManager: GridLayoutManager
    ) {
        val spanCount = layoutManager.spanCount
        val spanSizeLookup = layoutManager.spanSizeLookup
        val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount)
        val spanSize = spanSizeLookup.getSpanSize(position).coerceAtMost(spanCount)
        val spanGroupIndex = spanSizeLookup.getSpanGroupIndex(position, spanCount)
        val lastGroupIndex = spanSizeLookup.getSpanGroupIndex(itemCount - 1, spanCount)
        val edge = if (includeEdge && divider == null) edgeSpacing else 0
        val horizontal = activeHorizontalSpacing()
        val vertical = activeVerticalSpacing()

        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            outRect.left = startOffset(spanIndex, spanCount, horizontal, edge)
            outRect.right = endOffset(spanIndex, spanSize, spanCount, horizontal, edge)
            outRect.top = when {
                spanGroupIndex == 0 -> edge
                else -> vertical
            }
            outRect.bottom = if (includeEdge && spanGroupIndex == lastGroupIndex) edge else 0
        } else {
            outRect.top = startOffset(spanIndex, spanCount, vertical, edge)
            outRect.bottom = endOffset(spanIndex, spanSize, spanCount, vertical, edge)
            outRect.left = when {
                spanGroupIndex == 0 -> edge
                else -> horizontal
            }
            outRect.right = if (includeEdge && spanGroupIndex == lastGroupIndex) edge else 0
        }
    }

    private fun applyStaggeredOffsets(
        outRect: Rect,
        view: View,
        position: Int,
        itemCount: Int,
        layoutManager: StaggeredGridLayoutManager
    ) {
        val layoutParams = view.layoutParams as? StaggeredGridLayoutManager.LayoutParams ?: return
        val spanCount = layoutManager.spanCount
        val spanIndex = if (layoutParams.isFullSpan) 0 else layoutParams.spanIndex
        val spanSize = if (layoutParams.isFullSpan) spanCount else 1
        val edge = if (includeEdge && divider == null) edgeSpacing else 0
        val horizontal = activeHorizontalSpacing()
        val vertical = activeVerticalSpacing()
        val isFirstGroup = position < spanCount
        val isLastGroup = position >= itemCount - spanCount

        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            outRect.left = startOffset(spanIndex, spanCount, horizontal, edge)
            outRect.right = endOffset(spanIndex, spanSize, spanCount, horizontal, edge)
            outRect.top = if (isFirstGroup) edge else vertical
            outRect.bottom = if (includeEdge && isLastGroup) edge else 0
        } else {
            outRect.top = startOffset(spanIndex, spanCount, vertical, edge)
            outRect.bottom = endOffset(spanIndex, spanSize, spanCount, vertical, edge)
            outRect.left = if (isFirstGroup) edge else horizontal
            outRect.right = if (includeEdge && isLastGroup) edge else 0
        }
    }

    private fun startOffset(
        spanIndex: Int,
        spanCount: Int,
        spacing: Int,
        edge: Int
    ): Int {
        return edge + spanIndex * (spacing - 2 * edge) / spanCount
    }

    private fun endOffset(
        spanIndex: Int,
        spanSize: Int,
        spanCount: Int,
        spacing: Int,
        edge: Int
    ): Int {
        return edge + (spanCount - spanIndex - spanSize) * (spacing - 2 * edge) / spanCount
    }

    private fun activeHorizontalSpacing(): Int {
        return if (divider != null) dividerSize else horizontalSpacing
    }

    private fun activeVerticalSpacing(): Int {
        return if (divider != null) dividerSize else verticalSpacing
    }

    private fun drawLinearDivider(canvas: Canvas, parent: RecyclerView) {
        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return
        val divider = divider ?: return

        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            val position = parent.getChildAdapterPosition(child)
            val itemCount = parent.adapter?.itemCount ?: 0
            if (position == RecyclerView.NO_POSITION) continue

            drawLinearChildDivider(canvas, parent, child, position, itemCount, layoutManager, divider)
        }
    }

    private fun drawLinearChildDivider(
        canvas: Canvas,
        parent: RecyclerView,
        child: View,
        position: Int,
        itemCount: Int,
        layoutManager: LinearLayoutManager,
        divider: Drawable
    ) {
        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            if (position == 0 && showFirstDivider) {
                drawHorizontalDivider(canvas, parent, child.top - dividerSize, child.top, divider)
            }
            if (position < itemCount - 1 || position == itemCount - 1 && showLastDivider) {
                drawHorizontalDivider(canvas, parent, child.bottom, child.bottom + dividerSize, divider)
            }
        } else {
            if (position == 0 && showFirstDivider) {
                drawVerticalDivider(canvas, parent, child.left - dividerSize, child.left, divider)
            }
            if (position < itemCount - 1 || position == itemCount - 1 && showLastDivider) {
                drawVerticalDivider(canvas, parent, child.right, child.right + dividerSize, divider)
            }
        }
    }

    private fun drawGridDivider(canvas: Canvas, parent: RecyclerView) {
        val layoutManager = parent.layoutManager as? GridLayoutManager ?: return
        val divider = divider ?: return
        val itemCount = parent.adapter?.itemCount ?: return
        val spanCount = layoutManager.spanCount
        val lookup = layoutManager.spanSizeLookup
        val lastGroup = lookup.getSpanGroupIndex(itemCount - 1, spanCount)

        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) continue

            val spanIndex = lookup.getSpanIndex(position, spanCount)
            val spanSize = lookup.getSpanSize(position).coerceAtMost(spanCount)
            val group = lookup.getSpanGroupIndex(position, spanCount)
            drawGridChildDivider(canvas, child, layoutManager, spanIndex, spanSize, group, lastGroup, divider)
        }
    }

    private fun drawGridChildDivider(
        canvas: Canvas,
        child: View,
        layoutManager: GridLayoutManager,
        spanIndex: Int,
        spanSize: Int,
        group: Int,
        lastGroup: Int,
        divider: Drawable
    ) {
        if (layoutManager.orientation == RecyclerView.VERTICAL) {
            if (spanIndex + spanSize < layoutManager.spanCount) {
                drawChildVerticalDivider(canvas, child, child.right, child.right + dividerSize, divider)
            }
            if (group < lastGroup) {
                drawChildHorizontalDivider(canvas, child, child.bottom, child.bottom + dividerSize, divider)
            }
        } else {
            if (spanIndex + spanSize < layoutManager.spanCount) {
                drawChildHorizontalDivider(canvas, child, child.bottom, child.bottom + dividerSize, divider)
            }
            if (group < lastGroup) {
                drawChildVerticalDivider(canvas, child, child.right, child.right + dividerSize, divider)
            }
        }
    }

    private fun drawStaggeredDivider(canvas: Canvas, parent: RecyclerView) {
        val layoutManager = parent.layoutManager as? StaggeredGridLayoutManager ?: return
        val divider = divider ?: return
        val itemCount = parent.adapter?.itemCount ?: return

        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            val position = parent.getChildAdapterPosition(child)
            val layoutParams = child.layoutParams as? StaggeredGridLayoutManager.LayoutParams
            if (position == RecyclerView.NO_POSITION || layoutParams == null) continue

            val spanIndex = layoutParams.spanIndex
            val isLastSpan = layoutParams.isFullSpan || spanIndex == layoutManager.spanCount - 1
            val isLastGroup = position >= itemCount - layoutManager.spanCount
            if (layoutManager.orientation == RecyclerView.VERTICAL) {
                if (!isLastSpan) {
                    drawChildVerticalDivider(canvas, child, child.right, child.right + dividerSize, divider)
                }
                if (!isLastGroup) {
                    drawChildHorizontalDivider(canvas, child, child.bottom, child.bottom + dividerSize, divider)
                }
            } else {
                if (!isLastSpan) {
                    drawChildHorizontalDivider(canvas, child, child.bottom, child.bottom + dividerSize, divider)
                }
                if (!isLastGroup) {
                    drawChildVerticalDivider(canvas, child, child.right, child.right + dividerSize, divider)
                }
            }
        }
    }

    private fun drawHorizontalDivider(
        canvas: Canvas,
        parent: RecyclerView,
        top: Int,
        bottom: Int,
        divider: Drawable
    ) {
        divider.setBounds(parent.paddingLeft, top, parent.width - parent.paddingRight, bottom)
        divider.draw(canvas)
    }

    private fun drawVerticalDivider(
        canvas: Canvas,
        parent: RecyclerView,
        left: Int,
        right: Int,
        divider: Drawable
    ) {
        divider.setBounds(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        divider.draw(canvas)
    }

    private fun drawChildHorizontalDivider(
        canvas: Canvas,
        child: View,
        top: Int,
        bottom: Int,
        divider: Drawable
    ) {
        divider.setBounds(child.left, top, child.right, bottom)
        divider.draw(canvas)
    }

    private fun drawChildVerticalDivider(
        canvas: Canvas,
        child: View,
        left: Int,
        right: Int,
        divider: Drawable
    ) {
        divider.setBounds(left, child.top, right, child.bottom)
        divider.draw(canvas)
    }

    class Builder {
        internal var divider: Drawable? = null
        internal var dividerSize: Int = 1
        internal var horizontalSpacing: Int = 0
        internal var verticalSpacing: Int = 0
        internal var edgeSpacing: Int = 0
        internal var includeEdge: Boolean = false
        internal var showFirstDivider: Boolean = false
        internal var showLastDivider: Boolean = false

        fun dividerColor(@ColorInt color: Int, size: Int = 1): Builder {
            divider = ColorDrawable(color)
            dividerSize = size
            return this
        }

        fun divider(drawable: Drawable, size: Int = 1): Builder {
            divider = drawable
            dividerSize = size
            return this
        }

        fun spacing(spacing: Int): Builder {
            horizontalSpacing = spacing
            verticalSpacing = spacing
            return this
        }

        fun horizontalSpacing(spacing: Int): Builder {
            horizontalSpacing = spacing
            return this
        }

        fun verticalSpacing(spacing: Int): Builder {
            verticalSpacing = spacing
            return this
        }

        fun edgeSpacing(edgeSpacing: Int): Builder {
            this.edgeSpacing = edgeSpacing
            includeEdge = true
            return this
        }

        fun includeEdge(include: Boolean): Builder {
            includeEdge = include
            return this
        }

        fun showFirstDivider(show: Boolean): Builder {
            showFirstDivider = show
            return this
        }

        fun showLastDivider(show: Boolean): Builder {
            showLastDivider = show
            return this
        }

        fun build(): SpaceItemDecoration {
            return SpaceItemDecoration(this)
        }
    }
}
