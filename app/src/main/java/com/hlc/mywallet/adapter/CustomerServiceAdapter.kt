package com.hlc.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.hlc.lib_base.extension.loadCircle
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.CustomerServiceResp
import com.hlc.mywallet.databinding.ItemCustomerServiceBinding

class CustomerServiceAdapter(
    private val onContactClick: (CustomerServiceResp) -> Unit
) : BaseQuickAdapter<CustomerServiceResp, CustomerServiceAdapter.VH>() {

    class VH(
        parent: ViewGroup,
        val binding: ItemCustomerServiceBinding = ItemCustomerServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    ) : QuickViewHolder(binding.root)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int, item: CustomerServiceResp?) {
        item ?: return
        holder.binding.apply {
            ivAvatar.loadCircle(R.drawable.ic_share_telegram)
            tvName.text = item.name.orEmpty()
            tvDesc.text = item.title.orEmpty()
            btnContact.onClick {
                onContactClick(item)
            }
        }
    }
}
