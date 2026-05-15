package com.hlc.mywallet.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.widget.SpaceItemDecoration
import com.hlc.mywallet.data.model.resp.BalanceType
import com.hlc.mywallet.databinding.DialogStringSelectBinding
import com.hlc.mywallet.databinding.ItemStringSelectBinding

class BalanceTypeSelectDialog : BaseListDialog<BalanceType, DialogStringSelectBinding>() {

    private val adapter by lazy {
        BalanceTypeAdapter { selectedItem ->
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
            adapter = this@BalanceTypeSelectDialog.adapter
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    SpaceItemDecoration.Builder()
                        .verticalSpacing(2)
                        .build()
                )
            }
        }
    }

    override fun bindItems(items: List<BalanceType>, selectedItem: BalanceType?) {
        adapter.submitSelectableList(items, selectedItem)
    }

    fun setOnItemSelectedListener(listener: (BalanceType) -> Unit): BalanceTypeSelectDialog {
        setOnItemSelectedListenerInternal(listener)
        return this
    }

    companion object {
        fun newInstance(
            items: List<BalanceType>,
            selectedItem: BalanceType? = null
        ): BalanceTypeSelectDialog {
            return BalanceTypeSelectDialog().apply {
                setItemsInternal(items)
                setSelectedItemInternal(selectedItem)
            }
        }
    }

    private class BalanceTypeAdapter(
        private val onItemSelected: (BalanceType) -> Unit
    ) : BaseSingleSelectListAdapter<BalanceType, BalanceTypeAdapter.VH>(onItemSelected) {

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

        override fun onBindViewHolder(holder: VH, position: Int, item: BalanceType?) {
            item ?: return
            holder.binding.apply {
                textView.text = item.label.orEmpty()
                radioButton.isChecked = isSelected(position)
                root.setOnClickListener {
                    updateSelectedPosition(holder.bindingAdapterPosition)
                }
                radioButton.setOnClickListener {
                    updateSelectedPosition(holder.bindingAdapterPosition)
                }
            }
        }

        override fun areItemsTheSame(left: BalanceType, right: BalanceType): Boolean {
            return left.value == right.value
        }
    }
}
