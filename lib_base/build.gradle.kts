plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val appConfig = rootProject.extra["AppConfig"] as Map<*, *>

android {
    namespace = "com.hlc.lib_base"
    compileSdk = appConfig["compileSdk"] as Int

    defaultConfig {
        minSdk = appConfig["minSdk"] as Int
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        val javaVersion = JavaVersion.toVersion(appConfig["javaVersion"] as Int)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    
    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        }
    }
}

configurations.all {
    exclude(group = "com.android.support", module = "support-compat")
    exclude(group = "com.android.support", module = "support-core-utils")
    exclude(group = "com.android.support", module = "support-core-ui")
    exclude(group = "com.android.support", module = "support-fragment")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.activity)
    api(libs.androidx.lifecycle.runtime.ktx)

    // 基础 Android
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)

    // Fragment
    api(libs.androidx.fragment.ktx)

    // 生命周期
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.viewmodel.savedstate)

    // 协程
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.core)

    // 网络
    api(libs.retrofit)
    api(libs.okhttp)
    api(libs.logging.interceptor)

    // Moshi
    api(libs.moshi)
    api(libs.moshi.kotlin)
    
    // ImmersionBar
    api(libs.immersionbar)
    api(libs.immersionbar.ktx)
    
    // AndroidAutoSize
    api(libs.autosize)
    
    // ShapeView
    api(libs.shapeview)
    
    // XXPermissions
    api(libs.xxpermissions)
    
    // Toaster
    api(libs.toaster)
}
