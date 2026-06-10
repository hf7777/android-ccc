# App 模块 ProGuard / R8 规则
# lib_base 的 consumer-rules.pro 会自动合并

# ---------------------------------------------------------------------------
# 调试堆栈（release 崩溃可读行号）
# ---------------------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---------------------------------------------------------------------------
# ViewBinding / 基类反射（与 lib_base consumer-rules 互补）
# ---------------------------------------------------------------------------

-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes *Annotation*

-keep class * extends com.hlc.lib_base.BaseVbActivity { *; }
-keep class * extends com.hlc.lib_base.BaseVbFragment { *; }
-keep class * extends com.hlc.lib_base.BaseLazyFragment { *; }
-keep class * extends com.hlc.lib_base.BaseLazyListFragment { *; }
-keep class * extends com.hlc.lib_base.BaseBottomSheetDialog { *; }

-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(android.view.LayoutInflater);
    public static *** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
    public static *** bind(android.view.View);
}

-keep class **.databinding.** { *; }
-keep class **.*Binding { *; }

# lib_base 内 Web 页 Binding（包名与 app 不同）
-keep class com.hlc.lib_base.databinding.** { *; }
-keep class com.hlc.lib_base.web.WebActivity { *; }

# ---------------------------------------------------------------------------
# Moshi 实体（KotlinJsonAdapterFactory 反射解析 data class）
# ---------------------------------------------------------------------------
-keep class kotlin.Metadata { *; }

-keep class com.hlc.mywallet.data.model.** { *; }
-keep class com.hlc.mywallet.feature.wallet.bean.** { *; }
-keep class com.hlc.mywallet.feature.bonus.bean.** { *; }
-keep class com.hlc.mywallet.feature.mine.bean.** { *; }

-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

-keepclasseswithmembers class * {
    @com.squareup.moshi.* <fields>;
}

# ---------------------------------------------------------------------------
# Parcelable（页面跳转 Bundle / Dialog 传参）
# ---------------------------------------------------------------------------
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class * implements android.os.Parcelable {
    public static ** CREATOR;
}

-keep @kotlinx.parcelize.Parcelize class * { *; }

# ---------------------------------------------------------------------------
# Retrofit Service 接口
# ---------------------------------------------------------------------------
-keep,allowobfuscation,allowshrinking interface com.hlc.mywallet.data.api.** { *; }

-dontwarn retrofit2.**
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Kotlin 协程 + Retrofit suspend
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ---------------------------------------------------------------------------
# DataStore 缓存（Moshi 按 Class 序列化）
# ---------------------------------------------------------------------------
-keep class com.hlc.mywallet.storage.** { *; }

# ---------------------------------------------------------------------------
# Hilt / Dagger
# ---------------------------------------------------------------------------
-dontwarn com.google.errorprone.annotations.**

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclassmembers class * {
    @dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper <init>(...);
}

# ---------------------------------------------------------------------------
# Application / BuildConfig
# ---------------------------------------------------------------------------
-keep class com.hlc.mywallet.App { *; }
-keep class com.hlc.mywallet.BuildConfig { *; }

# ---------------------------------------------------------------------------
# 路由 / 拦截器
# ---------------------------------------------------------------------------
-keep class com.hlc.mywallet.router.** { *; }
-keep class com.hlc.mywallet.manager.** { *; }

-keep class com.hlc.mywallet.feature.**Activity { *; }
-keep class com.hlc.mywallet.feature.**Fragment { *; }

# Dialog（含手写 inflate 的 DialogFragment，避免被裁剪）
-keep class com.hlc.mywallet.dialog.** { *; }

# ---------------------------------------------------------------------------
# EventBus：以 Class.getName() 作为事件 key，实现类不能被混淆改名
# ---------------------------------------------------------------------------
-keep class com.hlc.mywallet.common.AppEvent { *; }
-keep class com.hlc.mywallet.common.AppEvent$* { *; }
-keep class com.hlc.mywallet.common.AppUpdateCheckEvent { *; }
-keep class com.hlc.mywallet.common.AppUpdateCheckEvent$* { *; }

# ---------------------------------------------------------------------------
# 业务枚举 / 下载状态（when 分支 + Moshi 枚举字段）
# ---------------------------------------------------------------------------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.hlc.mywallet.feature.deposit.bean.DepositStatus { *; }
-keep class com.hlc.mywallet.common.AppUpdateDownloader$DownloadState { *; }
-keep class com.hlc.mywallet.common.AppUpdateDownloader$DownloadState$* { *; }

# ---------------------------------------------------------------------------
# Glide
# ---------------------------------------------------------------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# ---------------------------------------------------------------------------
# WebView / AgentWeb（仅保留 JS 接口；支付宝/下载为可选依赖，用 dontwarn）
# ---------------------------------------------------------------------------
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-dontwarn com.just.agentweb.**

# ---------------------------------------------------------------------------
# Banner、沉浸式状态栏
# ---------------------------------------------------------------------------
-keep class com.youth.banner.** { *; }
-keep class com.gyf.immersionbar.** { *; }

# ---------------------------------------------------------------------------
# 其他三方
# ---------------------------------------------------------------------------
-dontwarn org.jetbrains.annotations.**
-dontwarn com.blankj.utilcode.**

# AgentWeb 可选依赖（未集成支付宝 SDK / 下载库时由 R8 忽略）
-dontwarn com.alipay.sdk.**
-dontwarn com.download.library.**
