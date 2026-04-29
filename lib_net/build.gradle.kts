plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

val appConfig = rootProject.extra["AppConfig"] as Map<*, *>

android {
    namespace = "com.hlc.lib_net"
    compileSdk = appConfig["compileSdk"] as Int

    defaultConfig {
        minSdk = appConfig["minSdk"] as Int
    }

    compileOptions {
        val javaVersion = JavaVersion.toVersion(appConfig["javaVersion"] as Int)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

dependencies {
    implementation(project(":lib_base"))
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit.moshi)
}
