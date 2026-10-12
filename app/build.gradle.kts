plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

val appConfig = rootProject.extra["AppConfig"] as Map<*, *>

android {
    namespace = "com.hlc.mywallet"
    compileSdk = appConfig["compileSdk"] as Int

    defaultConfig {
        applicationId = "com.hlc.mywallet"
        minSdk = appConfig["minSdk"] as Int
        targetSdk = appConfig["targetSdk"] as Int
        versionCode = appConfig["versionCode"] as Int
        versionName = appConfig["versionName"] as String
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "ADJUST_APP_TOKEN", "\"mbyir9b045q8\"")
    }

    buildTypes {
        debug {
            // vicent
//            buildConfigField("String", "BASE_URL", "\"http://192.168.8.175:3115\"")
            // max
            buildConfigField("String", "BASE_URL", "\"http://192.168.8.154:3115\"")
//            buildConfigField("String", "BASE_URL", "\"https://appapi.aapay.app\"")
            buildConfigField("String", "ENV", "\"DEV\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
//            buildConfigField("String", "BASE_URL", "\"http://192.168.8.154:3115\"")
            buildConfigField("String", "BASE_URL", "\"https://appapi.aapay.app\"")
            buildConfigField("String", "ENV", "\"RELEASE\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

android.applicationVariants.configureEach {
    outputs.configureEach {
        val apkOutput = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
        apkOutput.outputFileName = "AApay_v$versionName.apk"
    }
}

configurations.all {
    exclude(group = "com.android.support", module = "support-compat")
    exclude(group = "com.android.support", module = "support-core-utils")
    exclude(group = "com.android.support", module = "support-core-ui")
    exclude(group = "com.android.support", module = "support-fragment")
}

dependencies {
    implementation(project(":lib_base"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.datastore.preferences)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // Moshi (needed for NetworkModule)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.moshi)
    
    // ImmersionBar
    implementation(libs.immersionbar)
    implementation(libs.immersionbar.ktx)
    
    // Media3：教程 MP4 直链播放（仅 exoplayer 模块）
    implementation(libs.androidx.media3.exoplayer)

    // Adjust：归因与事件追踪（Google Play 版本）
    implementation(libs.adjust)
    implementation(libs.installreferrer)
    implementation(libs.play.services.ads.identifier)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    // Banner
    implementation(libs.banner)
    
    // BaseRecyclerViewAdapterHelper
    implementation(libs.brvah)
    debugImplementation(libs.leakcanary.android)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.17.0"))
}
