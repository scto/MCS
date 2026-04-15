plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.scto.mcs.feature.settings"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
// build.gradle.kts for the feature:settings module
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    // Add version catalog plugin if used in the project
    // id("libs.plugins.android.application") // Example if using version catalogs
    // id("libs.plugins.kotlin.android")      // Example if using version catalogs
}

android {
    namespace = "com.scto.mcs.feature.settings"
    compileSdk = 34 // Or your project's compile SDK version

    defaultConfig {
        minSdk = 24 // Or your project's min SDK version
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
        // Use the appropriate Compose compiler version for your Kotlin version
        // Example: kotlinCompilerExtensionVersion = "1.5.1" 
        // If you don't know it, you might need to check the root project's build.gradle.kts or gradle.properties
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get() // Assuming version catalog usage
    }
}

dependencies {
    // Core Compose dependencies
    implementation("androidx.core:core-ktx:1.12.0") // Use latest stable version
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") // Use latest stable version
    implementation("androidx.activity:activity-compose:1.8.2") // Use latest stable version
    implementation(platform(libs.androidx.compose.bom)) // Using version catalog
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.7") // Use latest stable version

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Use latest stable version
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Use latest stable version

    // ViewModel (if needed within this module)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // --- Project Specific Dependencies ---
    // Assuming core modules are available as projects
    implementation(project(":core:ui")) // Assuming core:ui contains components like DirectorySelector, ColorPickerDialog
    implementation(project(":core:utils")) // Assuming core:utils contains ThemeState, LogConfigState, WorkspaceManager, BuildConfig
    implementation(project(":core:navigation")) // Assuming core:navigation provides NavController extensions or similar

    // AboutLibraries dependency
    implementation("com.mikepenz:aboutlibraries-compose:10.0.2") // Check for latest version

    // Coil for image loading in AboutScreen
    implementation("io.coil-kt:coil-compose:2.6.0") // Check for latest version

    // --- Testing Dependencies ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Match BOM version
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
