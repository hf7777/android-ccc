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
import com.hlc.mywallet.databinding.DialogStatusDescBinding

class StatusDescDialog : DialogFragment() {

    private var _binding: DialogStatusDescBinding? = null
    private val binding: DialogStatusDescBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        _binding = DialogStatusDescBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
    companion object {
        private const val DIALOG_WIDTH_RATIO = 0.80f

        fun newInstance(): StatusDescDialog {
            return StatusDescDialog()
        }
    }
}
