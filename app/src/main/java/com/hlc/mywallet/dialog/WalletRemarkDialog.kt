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
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.req.RemarkReq
import com.hlc.mywallet.data.model.resp.Wallet
import com.hlc.mywallet.databinding.DialogWalletRemarkBinding

class WalletRemarkDialog : DialogFragment() {

    private var _binding: DialogWalletRemarkBinding? = null
    private val binding: DialogWalletRemarkBinding get() = _binding!!

    private var wallet: Wallet? = null
    private var onSaveListener: ((RemarkReq) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wallet = arguments?.let {
            BundleCompat.getParcelable(it, ARG_WALLET, Wallet::class.java)
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
        _binding = DialogWalletRemarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etRemark.setText(wallet?.remark.orEmpty())
        binding.btnClose.onClick { dismiss() }
        binding.btnSave.onClick {
            val phone = wallet?.phone.orEmpty()
            val channelCode = wallet?.channelCode.orEmpty()
            if (phone.isEmpty() || channelCode.isEmpty()) return@onClick
            val req = RemarkReq(
                phone = phone,
                channelCode = channelCode,
                remark = binding.etRemark.text.toString().trim()
            )
            onSaveListener?.invoke(req)
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

    fun setOnSaveListener(listener: (RemarkReq) -> Unit): WalletRemarkDialog {
        onSaveListener = listener
        return this
    }

    companion object {
        private const val ARG_WALLET = "wallet"
        private const val DIALOG_WIDTH_RATIO = 0.80f

        fun newInstance(wallet: Wallet): WalletRemarkDialog {
            return WalletRemarkDialog().apply {
                arguments = bundleOf(ARG_WALLET to wallet)
            }
        }
    }
}
