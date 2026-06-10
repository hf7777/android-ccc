# ========== 通用属性（反射 / 泛型 / 注解 / 堆栈行号）==========
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ========== ViewBinding 反射 ==========
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
-keep class com.hlc.lib_base.databinding.** { *; }

# Moshi 自定义适配器注解
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# ========== 网络层（Moshi + Retrofit）==========
-keep class kotlin.Metadata { *; }

-keep class com.hlc.lib_base.net.BaseResponse { *; }
-keep class com.hlc.lib_base.net.ApiException { *; }
-keep class com.hlc.lib_base.net.ApiException$* { *; }
-keep class com.hlc.lib_base.net.ApiResult { *; }
-keep class com.hlc.lib_base.net.ApiResult$* { *; }

-keep,allowobfuscation,allowshrinking interface * {
    @retrofit2.http.* <methods>;
}

-dontwarn retrofit2.**
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
