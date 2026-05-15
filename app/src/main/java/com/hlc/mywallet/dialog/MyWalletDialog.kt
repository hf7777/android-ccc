package com.hlc.mywallet.dialog

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hlc.lib_base.BaseBottomSheetDialog
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.adapter.MyWalletAdapter
import com.hlc.mywallet.common.WalletIconMapper
import com.hlc.mywallet.data.model.resp.MyWalletResp
import com.hlc.mywallet.databinding.DialogMyWalletBinding

/**
 * 钱包弹窗
 */
class MyWalletDialog : BaseBottomSheetDialog<DialogMyWalletBinding>() {

    private var wallets: List<MyWalletResp>? = null
    private var onConfirmListener: ((MyWalletResp) -> Unit)? = null
    private val walletAdapter by lazy {
        MyWalletAdapter(WalletIconMapper::getIconRes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallets = arguments?.getParcelableArrayList(KEY_WALLETS)
    }

    override fun initView() {
        binding.rvWallet.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = walletAdapter
        }
        binding.btnCancel.onClick { dismiss() }
        binding.btnConfirm.onClick {
            walletAdapter.getSelectedWallet()?.let { wallet ->
                onConfirmListener?.invoke(wallet)
            }
            dismiss()
        }
        walletAdapter.submitWallets(wallets.orEmpty())
    }

    override fun getMaxHeight(): Int {
        return 572.dp
    }

    companion object {
        private const val KEY_WALLETS = "wallets"

        fun newInstance(wallets: List<MyWalletResp>): MyWalletDialog {
            return MyWalletDialog().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(KEY_WALLETS, ArrayList(wallets))
                }
            }
        }
    }

    fun setOnConfirmListener(listener: (MyWalletResp) -> Unit): MyWalletDialog {
        onConfirmListener = listener
        return this
    }
}
