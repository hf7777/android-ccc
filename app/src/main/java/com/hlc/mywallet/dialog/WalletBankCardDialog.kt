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
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.KeyboardUtils
import com.hlc.lib_base.extension.enableWhenAllFilled
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.req.WalletBankInfoReq
import com.hlc.mywallet.data.model.resp.WalletBankInfoResp
import com.hlc.mywallet.databinding.DialogWalletBankCardBinding
import com.hlc.mywallet.extension.setupPasswordVisibilityToggle

class WalletBankCardDialog : DialogFragment() {

    private var _binding: DialogWalletBankCardBinding? = null
    private val binding: DialogWalletBankCardBinding get() = _binding!!

    private var walletId: String = ""
    private var upi: String = ""
    private var isEditMode = false
    private var bankInfo: WalletBankInfoResp? = null
    private var onSubmitListener: ((WalletBankInfoReq) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walletId = arguments?.getString(ARG_WALLET_ID).orEmpty()
        upi = arguments?.getString(ARG_UPI).orEmpty()
        isEditMode = arguments?.getBoolean(ARG_IS_EDIT) == true
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
                setCanceledOnTouchOutside(false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWalletBankCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvUpi.text = upi
        binding.etUpiPin.setupPasswordVisibilityToggle()
        binding.btnClose.onClick { dismiss() }
        if (isEditMode) {
            bankInfo?.let { info ->
                binding.etAccountNo.setText(info.accountNo.orEmpty())
                binding.etBankName.setText(info.bankName.orEmpty())
                binding.etUpiPin.setText(info.upiPin.orEmpty())
            }
        }
        binding.btnCheckPin.enableWhenAllFilled(
            binding.etAccountNo,
            binding.etBankName,
            binding.etUpiPin
        )
        binding.btnCheckPin.onClick {
            val req = WalletBankInfoReq(
                walletId = walletId,
                accountNo = binding.etAccountNo.text.toString().trim(),
                bankName = binding.etBankName.text.toString().trim(),
                upiPin = binding.etUpiPin.text.toString().trim()
            )
            KeyboardUtils.hideSoftInput(binding.etUpiPin)
            onSubmitListener?.invoke(req)
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

    fun setOnSubmitListener(listener: (WalletBankInfoReq) -> Unit): WalletBankCardDialog {
        onSubmitListener = listener
        return this
    }

    fun setOnCheckPinListener(listener: (WalletBankInfoReq) -> Unit): WalletBankCardDialog {
        return setOnSubmitListener(listener)
    }

    companion object {
        private const val ARG_WALLET_ID = "wallet_id"
        private const val ARG_UPI = "upi"
        private const val ARG_IS_EDIT = "is_edit"
        private const val ARG_BANK_INFO = "bank_info"
        private const val DIALOG_WIDTH_RATIO = 0.80f

        fun newInstance(walletId: String, upi: String?): WalletBankCardDialog {
            return WalletBankCardDialog().apply {
                arguments = bundleOf(
                    ARG_WALLET_ID to walletId,
                    ARG_UPI to upi.orEmpty(),
                    ARG_IS_EDIT to false
                )
            }
        }

        fun newInstanceForEdit(
            walletId: String,
            upi: String?,
            bankInfo: WalletBankInfoResp
        ): WalletBankCardDialog {
            return WalletBankCardDialog().apply {
                arguments = bundleOf(
                    ARG_WALLET_ID to walletId,
                    ARG_UPI to upi.orEmpty(),
                    ARG_IS_EDIT to true,
                    ARG_BANK_INFO to bankInfo
                )
            }
        }
    }
}
