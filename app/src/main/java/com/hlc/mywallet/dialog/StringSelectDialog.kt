package com.hlc.mywallet.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.databinding.DialogStringSelectBinding
import com.hlc.mywallet.databinding.ItemStringSelectBinding

class StringSelectDialog : BaseListDialog<String, DialogStringSelectBinding>() {

    private val adapter by lazy {
        StringListAdapter { selectedItem ->
            dispatchItemSelected(selectedItem)
        }
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogStringSelectBinding {
        return DialogStringSelectBinding.inflate(inflater, container, false)
    }

    override fun onBindingCreated(binding: DialogStringSelectBinding) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@StringSelectDialog.adapter
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    SpaceItemDecoration.Builder()
                        .verticalSpacing(2)
                        .build()
                )
            }
        }
    }

    override fun bindItems(items: List<String>, selectedItem: String?) {
        adapter.submitSelectableList(items, selectedItem)
    }

    fun setOnItemSelectedListener(listener: (String) -> Unit): StringSelectDialog {
        setOnItemSelectedListenerInternal(listener)
        return this
    }

    companion object {
        fun newInstance(items: List<String>, selectedItem: String = ""): StringSelectDialog {
            return StringSelectDialog().apply {
                setItemsInternal(items)
                setSelectedItemInternal(selectedItem.ifEmpty { null })
            }
        }
    }

    private class StringListAdapter(
        private val onItemSelected: (String) -> Unit
    ) : BaseSingleSelectListAdapter<String, StringListAdapter.VH>(onItemSelected) {

        class VH(
            parent: ViewGroup,
            val binding: ItemStringSelectBinding = ItemStringSelectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) : QuickViewHolder(binding.root)

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }

        override fun onBindViewHolder(holder: VH, position: Int, item: String?) {
            item ?: return
            holder.binding.apply {
                textView.text = item
                radioButton.isChecked = isSelected(position)
                root.setOnClickListener {
                    updateSelectedPosition(holder.bindingAdapterPosition)
                }
                radioButton.setOnClickListener {
                    updateSelectedPosition(holder.bindingAdapterPosition)
                }
            }
        }
    }
}
