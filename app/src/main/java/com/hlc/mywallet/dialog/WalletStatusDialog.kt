package com.hlc.mywallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.Wallet
import com.hlc.mywallet.databinding.DialogWalletStatusBinding

class WalletStatusDialog : DialogFragment() {

    private var wallet: Wallet? = null
    private var _binding: DialogWalletStatusBinding? = null
    private val binding: DialogWalletStatusBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallet = arguments?.getParcelable(KEY_WALLET)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.apply {
                setBackgroundDrawableResource(R.color.transparent)
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.CENTER)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWalletStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallet?.let(::bindWallet)
        binding.btnClose.onClick { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindWallet(wallet: Wallet) {
        binding.tvName.text = wallet.channelName.orEmpty() + " Status"
        binding.tvChannel.text = wallet.channelName.orEmpty()
        binding.tvPhone.text = wallet.phone.orEmpty()
        binding.tvUpi.text = wallet.upi.orEmpty()
        binding.tvOnlineStatus.text = mapOnlineStatus(wallet.onlineStatus)
        binding.tvWalletStatus.text = mapStatus(wallet.status)
        binding.tvSellingStatus.text = mapStatus(wallet.sellStatus)
        binding.tvAutoBuyStatus.text = mapStatus(wallet.autoBuyStatus)
    }

    private fun mapOnlineStatus(status: String?): String {
        return when (status) {
            ONLINE_STATUS_ENABLE -> getString(R.string.available)
            ONLINE_STATUS_DISABLE -> getString(R.string.pause)
            else -> status.orEmpty()
        }
    }

    private fun mapStatus(status: String?): String {
        return when (status) {
            SWITCH_STATUS_ENABLE -> getString(R.string.enable)
            SWITCH_STATUS_CLOSE -> getString(R.string.close)
            else -> status.orEmpty()
        }
    }


    companion object {
        private const val KEY_WALLET = "wallet"
        private const val DIALOG_WIDTH_RATIO = 0.80f
        private const val ONLINE_STATUS_ENABLE = "Y"
        private const val ONLINE_STATUS_DISABLE = "N"
        private const val SWITCH_STATUS_ENABLE = "enable"
        private const val SWITCH_STATUS_CLOSE = "close"

        fun newInstance(wallet: Wallet): WalletStatusDialog {
            return WalletStatusDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_WALLET, wallet)
                }
            }
        }
    }
}
