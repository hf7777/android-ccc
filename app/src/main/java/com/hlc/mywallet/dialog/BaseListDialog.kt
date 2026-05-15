package com.hlc.mywallet.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.hlc.mywallet.R

abstract class BaseListDialog<T, VB : ViewBinding> : DialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    private var items: List<T> = emptyList()
    private var selectedItem: T? = null
    private var onItemSelectedListener: ((T) -> Unit)? = null

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
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindingCreated(binding)
        bindItems(items, selectedItem)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * getDialogWidthRatio()).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected open fun onBindingCreated(binding: VB) = Unit

    protected abstract fun bindItems(items: List<T>, selectedItem: T?)

    protected open fun getDialogWidthRatio(): Float = 0.80f

    protected fun setItemsInternal(items: List<T>) {
        this.items = items
    }

    protected fun setSelectedItemInternal(selectedItem: T?) {
        this.selectedItem = selectedItem
    }

    protected fun setOnItemSelectedListenerInternal(listener: (T) -> Unit) {
        onItemSelectedListener = listener
    }

    protected fun dispatchItemSelected(item: T) {
        onItemSelectedListener?.invoke(item)
        dismiss()
    }
}
