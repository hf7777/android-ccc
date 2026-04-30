plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    id("com.google.dagger.hilt.android")
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
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://192.168.8.154:3115/\"")
            buildConfigField("String", "ENV", "\"DEV\"")
        }
        release {
            isMinifyEnabled = true
            buildConfigField("String", "BASE_URL", "\"https://api.production.com/\"")
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

configurations.all {
    exclude(group = "com.android.support", module = "support-compat")
    exclude(group = "com.android.support", module = "support-core-utils")
    exclude(group = "com.android.support", module = "support-core-ui")
    exclude(group = "com.android.support", module = "support-fragment")
}

dependencies {
    implementation(project(":lib_base"))
    implementation(project(":lib_storage"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    
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
    
    // Banner
    implementation(libs.banner)
    
    // BaseRecyclerViewAdapterHelper
    implementation(libs.brvah)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
