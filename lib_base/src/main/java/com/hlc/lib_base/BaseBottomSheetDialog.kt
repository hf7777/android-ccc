package com.hlc.lib_base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gyf.immersionbar.ktx.immersionBar

/**
 * 底部弹窗基类
 * 支持 ViewBinding 和沉浸式导航栏
 * 
 * 使用示例：
 * ```
 * class MyBottomSheet : BaseBottomSheetDialog<DialogMyBinding>() {
 *     override fun initView() {
 *         binding.tvTitle.text = "Title"
 *     }
 * }
 * ```
 */
abstract class BaseBottomSheetDialog<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupImmersiveNavigationBar()
        setupBottomSheetBehavior()
        initView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            // 设置背景透明，避免圆角被遮挡
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                // 设置软键盘模式
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
        }
    }

    /**
     * 创建 ViewBinding
     */
    private fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB {
        val clazz = resolveViewBindingClass<VB>()
        val method = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        @Suppress("UNCHECKED_CAST")
        return method.invoke(null, inflater, container, false) as VB
    }

    /**
     * 设置沉浸式导航栏
     */
    private fun setupImmersiveNavigationBar() {
        immersionBar {
            navigationBarColor(R.color.white)
            navigationBarDarkIcon(true)
        }
    }

    /**
     * 设置 BottomSheet 行为
     */
    private fun setupBottomSheetBehavior() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                // 设置初始状态为展开
                behavior.state = getInitialState()
                // 设置展开高度
                behavior.peekHeight = getPeekHeight()
                // 是否允许拖拽
                behavior.isDraggable = isDraggable()
                // 是否跳过折叠状态
                behavior.skipCollapsed = skipCollapsed()
                // 设置最大高度
                getMaxHeight()?.let { maxHeight ->
                    it.layoutParams.height = maxHeight
                    it.requestLayout()
                }
            }
        }
    }

    /**
     * 初始化视图
     * 子类重写此方法进行 UI 初始化
     */
    protected abstract fun initView()

    /**
     * 获取初始状态
     * 默认为展开状态
     */
    protected open fun getInitialState(): Int {
        return BottomSheetBehavior.STATE_EXPANDED
    }

    /**
     * 获取 PeekHeight
     * 默认为 0（完全展开）
     */
    protected open fun getPeekHeight(): Int {
        return 0
    }

    /**
     * 是否允许拖拽
     * 默认允许
     */
    protected open fun isDraggable(): Boolean {
        return true
    }

    /**
     * 是否跳过折叠状态
     * 默认跳过
     */
    protected open fun skipCollapsed(): Boolean {
        return true
    }

    /**
     * 获取最大高度
     * 默认为 null（不限制）
     * 返回具体数值可限制弹窗最大高度
     */
    protected open fun getMaxHeight(): Int? {
        return null
    }

    /**
     * 设置点击外部是否可关闭
     * 默认可关闭
     */
    protected open fun setCancelableOnTouchOutside(cancelable: Boolean) {
        dialog?.setCanceledOnTouchOutside(cancelable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
