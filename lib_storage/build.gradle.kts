plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val appConfig = rootProject.extra["AppConfig"] as Map<*, *>

android {
    namespace = "com.hlc.lib_storage"
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
    implementation(libs.androidx.datastore.preferences)
}
