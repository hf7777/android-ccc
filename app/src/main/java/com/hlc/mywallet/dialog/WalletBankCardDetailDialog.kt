package com.hlc.mywallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.WalletBankInfoResp
import com.hlc.mywallet.databinding.DialogWalletBankCardDetailBinding

class WalletBankCardDetailDialog : DialogFragment() {

    private var _binding: DialogWalletBankCardDetailBinding? = null
    private val binding: DialogWalletBankCardDetailBinding get() = _binding!!

    private var upi: String = ""
    private var bankInfo: WalletBankInfoResp? = null
    private var onUpdateListener: ((WalletBankInfoResp) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        upi = arguments?.getString(ARG_UPI).orEmpty()
        bankInfo = arguments?.let {
            BundleCompat.getParcelable(it, ARG_BANK_INFO, WalletBankInfoResp::class.java)
        }
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
        _binding = DialogWalletBankCardDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvUpi.text = upi
        bankInfo?.let(::bindBankInfo)
        binding.btnClose.onClick { dismiss() }
        binding.btnUpdate.onClick {
            bankInfo?.let { info ->
                onUpdateListener?.invoke(info)
            }
        }
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

    private fun bindBankInfo(info: WalletBankInfoResp) {
        binding.apply {
            tvAccountNo.text = info.accountNo.orEmpty()
            tvBankName.text = info.bankName.orEmpty()
            tvUpiPin.text = info.upiPin.orEmpty()
            ivAccountStatus.isVisible = info.accountNoStatus == STATUS_SUCCESS
            ivUpiPinStatus.isVisible = info.upiPinStatus == STATUS_SUCCESS
        }
    }

    fun setOnUpdateListener(listener: (WalletBankInfoResp) -> Unit): WalletBankCardDetailDialog {
        onUpdateListener = listener
        return this
    }

    companion object {
        private const val ARG_UPI = "upi"
        private const val ARG_BANK_INFO = "bank_info"
        private const val DIALOG_WIDTH_RATIO = 0.80f
        private const val STATUS_SUCCESS = "1"

        fun newInstance(upi: String?, bankInfo: WalletBankInfoResp): WalletBankCardDetailDialog {
            return WalletBankCardDetailDialog().apply {
                arguments = bundleOf(
                    ARG_UPI to upi.orEmpty(),
                    ARG_BANK_INFO to bankInfo
                )
            }
        }
    }
}
