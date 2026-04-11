plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.scto.mcs.core.common"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }
}
