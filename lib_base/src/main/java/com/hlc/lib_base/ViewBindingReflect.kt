package com.hlc.lib_base

import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 解析 BaseVbActivity / BaseVbFragment 等子类上的 ViewBinding 泛型参数。
 * release 混淆后若 Signature 丢失，则按类名约定回退（如 TutorialDetailActivity → ActivityTutorialDetailBinding）。
 */
@Suppress("UNCHECKED_CAST")
internal fun <VB : ViewBinding> Any.resolveViewBindingClass(): Class<VB> {
    findViewBindingClass(javaClass)?.let { return it as Class<VB> }

    resolveViewBindingByNaming(javaClass)?.let { return it as Class<VB> }

    throw IllegalStateException("Cannot resolve ViewBinding class for ${javaClass.name}")
}

private fun findViewBindingClass(clazz: Class<*>?): Class<out ViewBinding>? {
    var current: Class<*>? = clazz
    while (current != null) {
        val genericSuperclass = current.genericSuperclass
        if (genericSuperclass is ParameterizedType) {
            genericSuperclass.actualTypeArguments.forEach { argument ->
                argument.resolveBindingClass()?.let { return it }
            }
        }
        current = current.superclass
    }
    return null
}

private fun Type.resolveBindingClass(): Class<out ViewBinding>? {
    val clazz = when (this) {
        is Class<*> -> this
        is ParameterizedType -> rawType as? Class<*>
        else -> null
    } ?: return null

    return if (ViewBinding::class.java.isAssignableFrom(clazz)) {
        clazz as Class<out ViewBinding>
    } else {
        null
    }
}

/**
 * ViewBinding 类名约定（与 layout 生成的 Binding 一致）：
 * XxxActivity → ActivityXxxBinding
 * XxxFragment → FragmentXxxBinding
 * XxxDialog   → DialogXxxBinding
 */
private fun resolveViewBindingByNaming(hostClass: Class<*>): Class<out ViewBinding>? {
    val simpleName = hostClass.simpleName ?: return null
    val bindingSimpleName = when {
        simpleName.endsWith(ACTIVITY_SUFFIX) -> {
            "Activity${simpleName.removeSuffix(ACTIVITY_SUFFIX)}Binding"
        }
        simpleName.endsWith(FRAGMENT_SUFFIX) -> {
            "Fragment${simpleName.removeSuffix(FRAGMENT_SUFFIX)}Binding"
        }
        simpleName.endsWith(DIALOG_SUFFIX) -> {
            "Dialog${simpleName.removeSuffix(DIALOG_SUFFIX)}Binding"
        }
        else -> return null
    }

    val bindingPackage = resolveDatabindingPackage(hostClass)
    val bindingClassName = "$bindingPackage.$bindingSimpleName"
    return runCatching {
        @Suppress("UNCHECKED_CAST")
        Class.forName(bindingClassName) as Class<out ViewBinding>
    }.getOrNull()?.takeIf {
        ViewBinding::class.java.isAssignableFrom(it)
    }
}

/** 应用模块 databinding 包：取宿主类包名前 3 段 + .databinding（如 com.hlc.mywallet.databinding） */
private fun resolveDatabindingPackage(hostClass: Class<*>): String {
    val pkg = hostClass.`package`?.name
        ?: hostClass.name.substringBeforeLast('.')
    val appRoot = pkg.split('.').take(3).joinToString(".")
    return "$appRoot.databinding"
}

private const val ACTIVITY_SUFFIX = "Activity"
private const val FRAGMENT_SUFFIX = "Fragment"
private const val DIALOG_SUFFIX = "Dialog"
