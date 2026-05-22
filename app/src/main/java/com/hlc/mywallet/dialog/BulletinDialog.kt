package com.hlc.mywallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.hlc.lib_base.extension.dp
import com.hlc.lib_base.extension.gone
import com.hlc.lib_base.extension.loadRounded
import com.hlc.lib_base.extension.onClick
import com.hlc.lib_base.extension.visible
import com.hlc.lib_base.extension.visibleOrGone
import com.hlc.mywallet.R
import com.hlc.mywallet.data.model.resp.BulletinResp
import com.hlc.mywallet.databinding.DialogBulletinBinding

class BulletinDialog : DialogFragment() {

    private var _binding: DialogBulletinBinding? = null
    private val binding: DialogBulletinBinding get() = _binding!!

    private var onPrimaryActionListener: ((BulletinResp) -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    private val bulletin: BulletinResp by lazy {
        BulletinResp(
            autoConfirm = arguments?.getString(KEY_AUTO_CONFIRM),
            content = arguments?.getString(KEY_CONTENT),
            createBy = null,
            createTime = null,
            id = arguments?.getString(KEY_ID),
            imageUrl = arguments?.getString(KEY_IMAGE_URL),
            jumpRoute = arguments?.getString(KEY_JUMP_ROUTE),
            sortOrder = arguments?.getInt(KEY_SORT_ORDER),
            status = arguments?.getString(KEY_STATUS),
            title = arguments?.getString(KEY_TITLE),
            updateBy = null,
            updateTime = null
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
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
        _binding = DialogBulletinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindBulletin()
        binding.btnClose.onClick {
            dismissAllowingStateLoss()
        }
        binding.btnPrimary.onClick {
            onPrimaryActionListener?.invoke(bulletin)
            dismissAllowingStateLoss()
        }
        binding.scrollContent.setOnScrollChangeListener { _, _, _, _, _ ->
            updateScrollShadow()
        }
        binding.scrollContent.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateScrollShadow()
        }
        binding.ivBulletin.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateScrollableAreaHeight()
            updateScrollShadow()
        }
        binding.root.post {
            updateScrollableAreaHeight()
            updateScrollShadow()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialogWidth = (resources.displayMetrics.widthPixels - 56.dp).coerceAtMost(360.dp)
        dialog?.window?.setLayout(dialogWidth, WindowManager.LayoutParams.WRAP_CONTENT)
        binding.root.post {
            updateScrollableAreaHeight()
            updateScrollShadow()
        }
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        val listener = onDismissListener
        onDismissListener = null
        if (activity?.isFinishing == true || activity?.isDestroyed == true) {
            return
        }
        listener?.invoke()
    }

    override fun onDestroyView() {
        binding.ivBulletin.let { Glide.with(it).clear(it) }
        _binding = null
        onDismissListener = null
        super.onDestroyView()
    }

    fun setOnPrimaryActionListener(listener: (BulletinResp) -> Unit): BulletinDialog {
        onPrimaryActionListener = listener
        return this
    }

    fun setOnDismissListener(listener: () -> Unit): BulletinDialog {
        onDismissListener = listener
        return this
    }

    private fun bindBulletin() {
        binding.tvTitle.text = bulletin.title?.takeIf { it.isNotBlank() } ?: getString(R.string.dialog_title)
        binding.tvContent.text = HtmlCompat.fromHtml(
            bulletin.content.orEmpty(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvContent.movementMethod = LinkMovementMethod.getInstance()
        binding.btnPrimary.text = if (bulletin.isManualConfirm()) {
            getString(R.string.confirm)
        } else {
            getString(R.string.go)
        }

        val hasImage = bulletin.imageUrl.isNullOrBlank().not()
        val hasContent = bulletin.content.isNullOrBlank().not()
        binding.ivBulletin.visibleOrGone(hasImage)
        binding.tvContent.visibleOrGone(hasContent)
        binding.tvContent.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = if (hasImage) 16.dp else 0
        }

        if (hasImage) {
            binding.ivBulletin.loadRounded(bulletin.imageUrl, 14)
        } else {
            binding.ivBulletin.gone()
        }
    }

    /**
     * 内容区可能同时承载图片和富文本，底部渐变阴影只在还能继续向下滚动时展示。
     */
    private fun updateScrollShadow() {
        if (_binding == null) return
        val contentView = binding.scrollContent.getChildAt(0)
        val scrollHeight = binding.scrollContent.height
        val contentHeight = contentView?.height ?: 0
        val remainingScrollableHeight = contentHeight - scrollHeight - binding.scrollContent.scrollY

        if (scrollHeight > 0 && contentHeight > scrollHeight && remainingScrollableHeight > 0) {
            binding.viewScrollShadow.visible()
        } else {
            binding.viewScrollShadow.gone()
        }
    }

    private fun updateScrollableAreaHeight() {
        if (_binding == null) return

        val contentView = binding.scrollContent.getChildAt(0) ?: return
        val maxDialogHeight = (resources.displayMetrics.heightPixels * MAX_DIALOG_HEIGHT_RATIO).toInt()
        val reservedHeight = binding.layoutHeader.height +
            binding.layoutFooter.height +
            binding.layoutContentContainer.paddingTop +
            binding.layoutContentContainer.paddingBottom
        val maxScrollHeight = (maxDialogHeight - reservedHeight).coerceAtLeast(0)
        val contentHeight = contentView.measuredHeight

        binding.scrollContent.updateLayoutParams<ViewGroup.LayoutParams> {
            height = if (contentHeight == 0 || contentHeight <= maxScrollHeight || maxScrollHeight == 0) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                maxScrollHeight
            }
        }
        binding.scrollContent.post {
            updateScrollShadow()
        }
    }

    private fun BulletinResp.isManualConfirm(): Boolean {
        return autoConfirm.equals(AUTO_CONFIRM_YES, ignoreCase = true)
    }

    companion object {
        const val TAG = "BulletinDialog"

        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_CONTENT = "content"
        private const val KEY_IMAGE_URL = "image_url"
        private const val KEY_JUMP_ROUTE = "jump_route"
        private const val KEY_AUTO_CONFIRM = "auto_confirm"
        private const val KEY_SORT_ORDER = "sort_order"
        private const val KEY_STATUS = "status"
        private const val AUTO_CONFIRM_YES = "Y"
        private const val MAX_DIALOG_HEIGHT_RATIO = 0.7f

        fun newInstance(bulletin: BulletinResp): BulletinDialog {
            return BulletinDialog().apply {
                arguments = Bundle().apply {
                    putString(KEY_ID, bulletin.id)
                    putString(KEY_TITLE, bulletin.title)
                    putString(KEY_CONTENT, bulletin.content)
                    putString(KEY_IMAGE_URL, bulletin.imageUrl)
                    putString(KEY_JUMP_ROUTE, bulletin.jumpRoute)
                    putString(KEY_AUTO_CONFIRM, bulletin.autoConfirm)
                    putInt(KEY_SORT_ORDER, bulletin.sortOrder ?: Int.MAX_VALUE)
                    putString(KEY_STATUS, bulletin.status)
                }
            }
        }
    }
}
