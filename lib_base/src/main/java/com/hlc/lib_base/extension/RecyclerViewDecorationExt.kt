package com.hlc.lib_base.extension

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.hlc.lib_base.widget.SpaceItemDecoration

fun RecyclerView.addSpaceItemDecoration(
    spacing: Int,
    includeEdge: Boolean = false,
    edgeSpacing: Int = spacing
): SpaceItemDecoration {
    return addSpaceItemDecoration {
        spacing(spacing)
        if (includeEdge) {
            edgeSpacing(edgeSpacing)
        }
    }
}

fun RecyclerView.addSpaceItemDecoration(
    horizontalSpacing: Int,
    verticalSpacing: Int,
    includeEdge: Boolean = false,
    edgeSpacing: Int = horizontalSpacing
): SpaceItemDecoration {
    return addSpaceItemDecoration {
        horizontalSpacing(horizontalSpacing)
        verticalSpacing(verticalSpacing)
        if (includeEdge) {
            edgeSpacing(edgeSpacing)
        }
    }
}

fun RecyclerView.addDividerItemDecoration(
    @ColorInt color: Int,
    size: Int = 1,
    showFirstDivider: Boolean = false,
    showLastDivider: Boolean = false
): SpaceItemDecoration {
    return addSpaceItemDecoration {
        dividerColor(color, size)
        showFirstDivider(showFirstDivider)
        showLastDivider(showLastDivider)
    }
}

fun RecyclerView.addDividerItemDecoration(
    drawable: Drawable,
    size: Int = 1,
    showFirstDivider: Boolean = false,
    showLastDivider: Boolean = false
): SpaceItemDecoration {
    return addSpaceItemDecoration {
        divider(drawable, size)
        showFirstDivider(showFirstDivider)
        showLastDivider(showLastDivider)
    }
}

fun RecyclerView.addSpaceItemDecoration(
    builderAction: SpaceItemDecoration.Builder.() -> Unit
): SpaceItemDecoration {
    val decoration = SpaceItemDecoration.Builder()
        .apply(builderAction)
        .build()
    addItemDecoration(decoration)
    return decoration
}

fun RecyclerView.clearItemDecorations() {
    while (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
}
