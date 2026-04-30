package com.hlc.lib_base.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

/**
 * 监听多个输入框，自动控制按钮的启用/禁用状态
 * 只有所有输入框都有内容时，按钮才会启用
 * 
 * @param button 需要控制的按钮
 * @param editTexts 需要监听的输入框（可变参数）
 * 
 * 使用示例：
 * ```
 * binding.btnSubmit.enableWhenAllFilled(
 *     binding.etUsername,
 *     binding.etPassword,
 *     binding.etCode
 * )
 * ```
 */
fun Button.enableWhenAllFilled(vararg editTexts: EditText) {
    if (editTexts.isEmpty()) return
    
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            isEnabled = editTexts.all { it.text.toString().trim().isNotEmpty() }
        }
    }
    
    editTexts.forEach { it.addTextChangedListener(textWatcher) }
    
    // 初始检查
    isEnabled = editTexts.all { it.text.toString().trim().isNotEmpty() }
}

/**
 * 监听多个输入框，自动控制按钮的启用/禁用状态（带自定义验证规则）
 * 
 * @param editTexts 需要监听的输入框
 * @param validator 自定义验证函数，返回 true 时按钮启用
 * 
 * 使用示例：
 * ```
 * binding.btnSubmit.enableWhen(
 *     binding.etPhone,
 *     binding.etPassword
 * ) {
 *     val phone = binding.etPhone.text.toString()
 *     val password = binding.etPassword.text.toString()
 *     phone.length == 11 && password.length >= 6
 * }
 * ```
 */
fun Button.enableWhen(vararg editTexts: EditText, validator: () -> Boolean) {
    if (editTexts.isEmpty()) return
    
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            isEnabled = validator()
        }
    }
    
    editTexts.forEach { it.addTextChangedListener(textWatcher) }
    
    // 初始检查
    isEnabled = validator()
}

/**
 * 为 EditText 添加文本变化监听（简化版）
 * 
 * @param afterChanged 文本变化后的回调
 * 
 * 使用示例：
 * ```
 * binding.etSearch.onTextChanged { text ->
 *     viewModel.search(text)
 * }
 * ```
 */
fun EditText.onTextChanged(afterChanged: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            afterChanged(s?.toString() ?: "")
        }
    })
}
