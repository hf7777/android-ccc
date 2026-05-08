package com.hlc.lib_base.widget

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hlc.lib_base.R
import com.hlc.lib_base.databinding.DialogConfirmBinding
import com.hlc.lib_base.extension.dp

object ConfirmDialog {

    fun show(
        context: Context,
        title: String? = null,
        content: String,
        cancelText: String = context.getString(R.string.dialog_cancel),
        confirmText: String = context.getString(R.string.dialog_confirm),
        showCancelButton: Boolean = true,
        cancelable: Boolean = true,
        onCancelClick: (() -> Unit)? = null,
        onConfirmClick: (() -> Unit)? = null
    ): Dialog? {
        if (context is FragmentActivity && (context.isFinishing || context.isDestroyed)) {
            return null
        }

        val binding = DialogConfirmBinding.inflate(android.view.LayoutInflater.from(context))
        bindContent(
            binding = binding,
            title = title,
            content = content,
            cancelText = cancelText,
            confirmText = confirmText,
            showCancelButton = showCancelButton
        )

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(cancelable)
            .create()

        binding.tvCancel.setOnClickListener {
            onCancelClick?.invoke()
            dialog.dismiss()
        }
        binding.tvConfirm.setOnClickListener {
            onConfirmClick?.invoke()
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(cancelable)
        dialog.show()
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(300.dp, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        return dialog
    }

    fun show(
        fragment: Fragment,
        title: String? = null,
        content: String,
        cancelText: String = fragment.getString(R.string.dialog_cancel),
        confirmText: String = fragment.getString(R.string.dialog_confirm),
        showCancelButton: Boolean = true,
        cancelable: Boolean = true,
        onCancelClick: (() -> Unit)? = null,
        onConfirmClick: (() -> Unit)? = null
    ): Dialog? {
        if (!fragment.isAdded || fragment.isDetached) {
            return null
        }
        return show(
            context = fragment.requireContext(),
            title = title,
            content = content,
            cancelText = cancelText,
            confirmText = confirmText,
            showCancelButton = showCancelButton,
            cancelable = cancelable,
            onCancelClick = onCancelClick,
            onConfirmClick = onConfirmClick
        )
    }

    private fun bindContent(
        binding: DialogConfirmBinding,
        title: String?,
        content: String,
        cancelText: String,
        confirmText: String,
        showCancelButton: Boolean
    ) {
        binding.tvTitle.isVisible = !title.isNullOrEmpty()
        binding.tvTitle.text = title.orEmpty()
        binding.tvContent.text = content
        binding.tvCancel.text = cancelText
        binding.tvConfirm.text = confirmText

        binding.tvCancel.visibility = if (showCancelButton) View.VISIBLE else View.GONE
        binding.viewButtonDivider.visibility = if (showCancelButton) View.VISIBLE else View.GONE

        val confirmLayoutParams = binding.tvConfirm.layoutParams as ViewGroup.LayoutParams
        if (showCancelButton) {
            binding.tvConfirm.layoutParams =
                (confirmLayoutParams as LinearLayout.LayoutParams).apply {
                    width = 0
                    weight = 1f
                }
        } else {
            binding.tvConfirm.layoutParams =
                (confirmLayoutParams as LinearLayout.LayoutParams).apply {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    weight = 0f
                }
        }
    }
}

fun Context.showConfirmDialog(
    title: String? = null,
    content: String,
    cancelText: String = getString(R.string.dialog_cancel),
    confirmText: String = getString(R.string.dialog_confirm),
    showCancelButton: Boolean = true,
    cancelable: Boolean = true,
    onCancelClick: (() -> Unit)? = null,
    onConfirmClick: (() -> Unit)? = null
): Dialog? {
    return ConfirmDialog.show(
        context = this,
        title = title,
        content = content,
        cancelText = cancelText,
        confirmText = confirmText,
        showCancelButton = showCancelButton,
        cancelable = cancelable,
        onCancelClick = onCancelClick,
        onConfirmClick = onConfirmClick
    )
}

fun Fragment.showConfirmDialog(
    title: String? = null,
    content: String,
    cancelText: String = getString(R.string.dialog_cancel),
    confirmText: String = getString(R.string.dialog_confirm),
    showCancelButton: Boolean = true,
    cancelable: Boolean = true,
    onCancelClick: (() -> Unit)? = null,
    onConfirmClick: (() -> Unit)? = null
): Dialog? {
    return ConfirmDialog.show(
        fragment = this,
        title = title,
        content = content,
        cancelText = cancelText,
        confirmText = confirmText,
        showCancelButton = showCancelButton,
        cancelable = cancelable,
        onCancelClick = onCancelClick,
        onConfirmClick = onConfirmClick
    )
}
