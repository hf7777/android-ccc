package com.hlc.lib_base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.hlc.lib_base.R
import com.hlc.lib_base.databinding.LayoutPageStateBinding

class PageStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutPageStateBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        visibility = View.GONE
        isClickable = true
        isFocusable = true
    }

    fun showLoading(message: String = context.getString(R.string.loading)) {
        isVisible = true
        binding.layoutLoading.isVisible = true
        binding.layoutEmpty.isVisible = false
        binding.layoutError.isVisible = false
        binding.tvLoading.text = message
    }

    fun showEmpty(
        message: String = context.getString(R.string.page_empty),
        actionText: String? = null,
        onActionClick: (() -> Unit)? = null
    ) {
        isVisible = true
        binding.layoutLoading.isVisible = false
        binding.layoutEmpty.isVisible = true
        binding.layoutError.isVisible = false
        binding.tvEmpty.text = message
        bindAction(
            view = binding.tvEmptyAction,
            text = actionText,
            onClick = onActionClick
        )
    }

    fun showError(
        message: String = context.getString(R.string.page_error),
        actionText: String = context.getString(R.string.page_retry),
        onActionClick: (() -> Unit)? = null
    ) {
        isVisible = true
        binding.layoutLoading.isVisible = false
        binding.layoutEmpty.isVisible = false
        binding.layoutError.isVisible = true
        binding.tvError.text = message
        bindAction(
            view = binding.tvErrorAction,
            text = actionText,
            onClick = onActionClick
        )
    }

    fun showContent() {
        visibility = View.GONE
    }

    fun isShowingState(): Boolean {
        return visibility == View.VISIBLE
    }

    private fun bindAction(
        view: View,
        text: String?,
        onClick: (() -> Unit)?
    ) {
        val textView = view as android.widget.TextView
        val shouldShow = !text.isNullOrEmpty() && onClick != null
        textView.isVisible = shouldShow
        if (shouldShow) {
            textView.text = text
            textView.setOnClickListener { onClick?.invoke() }
        } else {
            textView.setOnClickListener(null)
        }
    }
}
