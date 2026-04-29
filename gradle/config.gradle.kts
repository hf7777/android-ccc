rootProject.extra["AppConfig"] = mapOf(
    "compileSdk" to AppConfig.compileSdk,
    "minSdk" to AppConfig.minSdk,
    "targetSdk" to AppConfig.targetSdk,
    "versionCode" to AppConfig.versionCode,
    "versionName" to AppConfig.versionName,
    "javaVersion" to AppConfig.javaVersion
)

object AppConfig {
    const val compileSdk = 34
    const val minSdk = 24
    const val targetSdk = 34
    const val versionCode = 1
    const val versionName = "1.0"
    
    const val javaVersion = 17
}
