package com.hlc.lib_base.extension

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.QuickAdapterHelper
import com.chad.library.adapter4.viewholder.QuickViewHolder

fun BaseQuickAdapter<*, *>.buildAdapterHelper(
    headerSize: Int = 0,
    footerSize: Int = 0,
    orientation: Int = RecyclerView.VERTICAL,
    builderAction: (QuickAdapterHelper.Builder.() -> Unit)? = null
): QuickAdapterHelper {
    val builder = QuickAdapterHelper.Builder(this)
    builderAction?.invoke(builder)
    return builder.build().addEdgeSpacingAdapters(
        headerSize = headerSize,
        footerSize = footerSize,
        orientation = orientation
    )
}

fun QuickAdapterHelper.addEdgeSpacingAdapters(
    headerSize: Int = 0,
    footerSize: Int = 0,
    orientation: Int = RecyclerView.VERTICAL
): QuickAdapterHelper {
    require(orientation == RecyclerView.VERTICAL || orientation == RecyclerView.HORIZONTAL) {
        "orientation must be RecyclerView.VERTICAL or RecyclerView.HORIZONTAL"
    }

    clearBeforeAdapters()
    clearAfterAdapters()

    if (headerSize > 0) {
        addBeforeAdapter(EdgeSpacingAdapter(headerSize, orientation))
    }
    if (footerSize > 0) {
        addAfterAdapter(EdgeSpacingAdapter(footerSize, orientation))
    }
    return this
}

private class EdgeSpacingAdapter(
    private val spaceSize: Int,
    private val orientation: Int
) : BaseQuickAdapter<Int, QuickViewHolder>() {

    init {
        submitList(listOf(spaceSize))
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        val view = View(context).apply {
            layoutParams = if (orientation == RecyclerView.HORIZONTAL) {
                ViewGroup.LayoutParams(spaceSize, ViewGroup.LayoutParams.MATCH_PARENT)
            } else {
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, spaceSize)
            }
        }
        return QuickViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Int?) = Unit
}
