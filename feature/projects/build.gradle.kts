plugins {
    id("com.android.dynamic.feature")
    id("org.jetbrains.kotlin.android")
    // Add other plugins as needed, e.g., kapt, hilt, etc.
}

android {
    namespace = "com.scto.mcs.feature.projects"

    compileSdk = 34 // Use your project's compile SDK version

    defaultConfig {
        minSdk = 24 // Use your project's min SDK version
        // Add other defaultConfig values if necessary
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        // Use your project's Kotlin compiler extension version
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

dependencies {
    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Core/Common UI Dependencies (Adjust based on your project structure)
    // Example: If core and ui-common are modules in your project
    implementation(project(":core:debug"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:utils"))

    // Hilt (if used)
    // implementation(libs.hilt.android)
    // kapt(libs.hilt.compiler)
    // implementation(libs.androidx.hilt.navigation.compose)
}
