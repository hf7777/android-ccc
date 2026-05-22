package com.hlc.mywallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.BundleCompat
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.ClipboardUtils
import com.hjq.toast.Toaster
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.OrderUsdt
import com.hlc.mywallet.databinding.DialogOrderDetailsBinding

class OrderDetailsDialog : DialogFragment() {

    private var order: OrderUsdt? = null
    private var _binding: DialogOrderDetailsBinding? = null
    private val binding: DialogOrderDetailsBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        order = arguments?.let {
            BundleCompat.getParcelable(it, KEY_ORDER, OrderUsdt::class.java)
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
        _binding = DialogOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        order?.let(::bindOrder)
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

    private fun bindOrder(order: OrderUsdt) {
        binding.tvToAddress.text = order.toAddress.orEmpty().ifEmpty { "-" }
        binding.tvTxHash.text = order.txHash.orEmpty().ifEmpty { "-" }
        binding.tvAmount.text = if (order.usdtAmount.isNullOrBlank()) {
            "-"
        } else {
            "${order.usdtAmount.formatNumber()} USDT"
        }

        binding.ivCopyAddress.onClick { copyText(order.toAddress) }
        binding.ivCopyHash.onClick { copyText(order.txHash) }
        binding.ivCopyAmount.onClick { copyText(order.usdtAmount) }
    }

    private fun copyText(text: String?) {
        val value = text?.trim().orEmpty()
        if (value.isEmpty()) {
            return
        }
        ClipboardUtils.copyText(value)
        Toaster.show(getString(R.string.copy_success))
    }

    companion object {
        private const val KEY_ORDER = "order"
        private const val DIALOG_WIDTH_RATIO = 0.80f

        fun newInstance(order: OrderUsdt): OrderDetailsDialog {
            return OrderDetailsDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_ORDER, order)
                }
            }
        }
    }
}
