package com.hlc.mywallet.dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.hjq.toast.Toaster
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visible
import com.hlc.mywallet.R
import com.hlc.mywallet.databinding.DialogUploadProofBinding

class UploadProofDialog : DialogFragment() {

    private var _binding: DialogUploadProofBinding? = null
    private val binding: DialogUploadProofBinding get() = _binding!!

    private var uploadedImageUrl: String? = null
    private var isUploadingImage = false
    private var onImageSelectedListener: ((Uri) -> Unit)? = null
    private var onSubmitListener: ((utr: String, voucherUrl: String) -> Unit)? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null || _binding == null) {
                return@registerForActivityResult
            }
            showSelectedImage(uri)
            uploadedImageUrl = null
            isUploadingImage = true
            updateSubmitState()
            onImageSelectedListener?.invoke(uri)
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            window?.apply {
                setBackgroundDrawableResource(R.color.transparent)
                setGravity(Gravity.CENTER)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUploadProofBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.onClick { dismiss() }
        binding.clSelectImage.onClick {
            imagePickerLauncher.launch("image/*")
        }
        binding.etUtr.doAfterTextChanged {
            updateSubmitState()
        }
        binding.btnSubmit.onClick {
            submit()
        }
        updateSubmitState()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * DIALOG_WIDTH_RATIO).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        binding.ivProofPreview.let { Glide.with(it).clear(it) }
        _binding = null
        super.onDestroyView()
    }

    fun setOnImageSelectedListener(listener: (Uri) -> Unit): UploadProofDialog {
        onImageSelectedListener = listener
        return this
    }

    fun setOnSubmitListener(listener: (utr: String, voucherUrl: String) -> Unit): UploadProofDialog {
        onSubmitListener = listener
        return this
    }

    fun onImageUploadSuccess(imageUrl: String) {
        uploadedImageUrl = imageUrl
        isUploadingImage = false
        if (_binding != null) {
            updateSubmitState()
        }
    }

    fun onImageUploadFailed() {
        uploadedImageUrl = null
        isUploadingImage = false
        if (_binding != null) {
            updateSubmitState()
        }
    }

    private fun showSelectedImage(uri: Uri) {
        binding.layoutImagePlaceholder.gone()
        binding.ivProofPreview.visible()
        Glide.with(binding.ivProofPreview)
            .asBitmap()
            .load(uri)
            .into(binding.ivProofPreview)
    }

    private fun updateSubmitState() {
        val hasUtr = !binding.etUtr.text?.toString()?.trim().isNullOrEmpty()
        binding.btnSubmit.isEnabled =
            hasUtr && !uploadedImageUrl.isNullOrEmpty() && !isUploadingImage
    }

    private fun submit() {
        if (isUploadingImage) {
            Toaster.show(getString(R.string.please_wait_for_image_upload))
            return
        }
        val utr = binding.etUtr.text?.toString()?.trim().orEmpty()
        if (utr.isEmpty()) {
            Toaster.show(getString(R.string.please_enter_transaction_id))
            return
        }
        val voucherUrl = uploadedImageUrl.orEmpty()
        if (voucherUrl.isEmpty()) {
            Toaster.show(getString(R.string.please_upload_transaction_screenshot))
            return
        }
        onSubmitListener?.invoke(utr, voucherUrl)
    }

    companion object {
        private const val DIALOG_WIDTH_RATIO = 0.80f

        fun newInstance(): UploadProofDialog {
            return UploadProofDialog()
        }
    }
}
