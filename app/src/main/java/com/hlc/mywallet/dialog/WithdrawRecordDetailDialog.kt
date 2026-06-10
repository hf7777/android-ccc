package com.hlc.mywallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.StringUtils
import com.hlc.lib_base.extension.formatNumber
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.WithdrawRecord
import com.hlc.mywallet.databinding.DialogWithdrawRecordDetailBinding

class WithdrawRecordDetailDialog : DialogFragment() {

    private var record: WithdrawRecord? = null
    private var _binding: DialogWithdrawRecordDetailBinding? = null
    private val binding: DialogWithdrawRecordDetailBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        record = arguments?.getParcelable(KEY_RECORD)
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
        _binding = DialogWithdrawRecordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        record?.let(::bindRecord)
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

    private fun bindRecord(record: WithdrawRecord) {
        binding.apply {
            tvBene.text = record.beneName.orEmpty()
            tvOrderNo.text = record.platformOrderNo.orEmpty()
            tvAmount.text = "${StringUtils.getString(R.string.price_symbol)}${record.orderAmount.orEmpty().formatNumber()}"
            tvStatus.text = record.status.orEmpty()
            tvCreateTime.text = record.createTime.orEmpty()
            tvAccountNo.text = record.accountNo.orEmpty()
            tvIfsc.text = record.ifsc.orEmpty()
            tvUtr.text = record.utr.orEmpty()
        }
    }

    companion object {
        private const val KEY_RECORD = "withdraw_record"
        private const val DIALOG_WIDTH_RATIO = 0.80f

        fun newInstance(record: WithdrawRecord): WithdrawRecordDetailDialog {
            return WithdrawRecordDetailDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_RECORD, record)
                }
            }
        }
    }
}
