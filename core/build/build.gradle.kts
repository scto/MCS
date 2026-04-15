plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.scto.mcs.core.build"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Core Domain
    implementation(project(":core:domain"))
    
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Kotlin Standard Library (implizit oft vorhanden, hier explizit zur Sicherheit)
    implementation(kotlin("stdlib"))

    // Hinweis: Die folgenden JARs/AARs müssen in den libs-Ordner oder über ein Repository verfügbar sein:
    // implementation("com.mcal:apksigner:x.y.z")
    // implementation("com.Day.Studio:axmleditor:x.y.z")
    
    // Fallback für lokale Entwicklung, falls Libraries als JAR vorliegen:
    fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))).forEach {
        implementation(it)
    }
}