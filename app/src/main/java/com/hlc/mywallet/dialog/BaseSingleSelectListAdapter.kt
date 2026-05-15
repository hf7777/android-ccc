package com.hlc.mywallet.dialog

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder

abstract class BaseSingleSelectListAdapter<T : Any, VH : QuickViewHolder>(
    private val onItemSelected: (T) -> Unit
) : BaseQuickAdapter<T, VH>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    protected fun isSelected(position: Int): Boolean = position == selectedPosition

    protected fun updateSelectedPosition(position: Int) {
        if (position == RecyclerView.NO_POSITION) {
            return
        }
        if (position == selectedPosition) {
            getSelectedItem()?.let(onItemSelected)
            return
        }
        val previousPosition = selectedPosition
        selectedPosition = position
        if (previousPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPosition)
        }
        notifyItemChanged(selectedPosition)
        getSelectedItem()?.let(onItemSelected)
    }

    fun submitSelectableList(items: List<T>, selectedItem: T? = null) {
        selectedPosition = when {
            items.isEmpty() -> RecyclerView.NO_POSITION
            selectedItem != null -> items.indexOfFirst { areItemsTheSame(it, selectedItem) }
                .takeIf { it >= 0 } ?: 0
            else -> 0
        }
        submitList(items)
    }

    protected fun getSelectedItem(): T? = items.getOrNull(selectedPosition)

    protected open fun areItemsTheSame(left: T, right: T): Boolean = left == right
}
