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
import com.hlc.lib_base.extension.enableWhenAllFilled
import com.hlc.lib_base.extension.onClick
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.req.BankInfoReq
import com.hlc.mywallet.data.model.resp.BankCard
import com.hlc.mywallet.databinding.DialogBankInfoBinding

class BankInfoDialog : DialogFragment() {

    private var _binding: DialogBankInfoBinding? = null
    private val binding: DialogBankInfoBinding get() = _binding!!

    private var bankCard: BankCard? = null
    private var onSaveListener: ((BankInfoReq, Boolean) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bankCard = arguments?.let {
            BundleCompat.getParcelable(it, ARG_BANK_CARD, BankCard::class.java)
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
        _binding = DialogBankInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isEditMode = bankCard != null
        binding.tvTitle.text = getString(
            if (isEditMode) R.string.edit_bank_info else R.string.add_bank
        )
        bankCard?.let { card ->
            binding.etAmountName.setText(card.accountName.orEmpty())
            binding.etAmountNumber.setText(card.accountNo.orEmpty())
            binding.etBeneficiaryName.setText(card.beneName.orEmpty())
            binding.etIfscCode.setText(card.ifsc.orEmpty())
        }
        binding.btnSave.enableWhenAllFilled(
            binding.etAmountName,
            binding.etAmountNumber,
            binding.etBeneficiaryName,
            binding.etIfscCode
        )
        binding.btnClose.onClick { dismiss() }
        binding.btnSave.onClick {
            val req = BankInfoReq(
                accountName = binding.etAmountName.text.toString().trim(),
                accountNo = binding.etAmountNumber.text.toString().trim(),
                beneName = binding.etBeneficiaryName.text.toString().trim(),
                ifsc = binding.etIfscCode.text.toString().trim()
            )
            onSaveListener?.invoke(req, isEditMode)
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

    fun setOnSaveListener(listener: (BankInfoReq, Boolean) -> Unit): BankInfoDialog {
        onSaveListener = listener
        return this
    }

    companion object {
        private const val ARG_BANK_CARD = "bank_card"
        private const val DIALOG_WIDTH_RATIO = 0.80f

        fun newInstance(bankCard: BankCard? = null): BankInfoDialog {
            return BankInfoDialog().apply {
                arguments = bundleOf(ARG_BANK_CARD to bankCard)
            }
        }
    }
}
