plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.scto.mcs.core.files"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        // Hinweis: Dieser Wert sollte idealerweise über libs.versions.toml gesteuert werden
        kotlinCompilerExtensionVersion = "1.5.15" 
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Interne Module
    implementation(project(":core:domain"))
    // Falls LogCatcher in einem anderen Modul liegt, hier hinzufügen:
    // implementation(project(":core:common"))

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Jetpack Compose & UI (benötigt für FileTree & FileIcons)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Icons (Material Extended & Third-Party)
    implementation(libs.androidx.material.icons.extended)
    // Bibliotheken für SimpleIcons und FontAwesomeIcons
    // Diese müssen in Ihrer libs.versions.toml definiert sein:
    // implementation(libs.composeIcons.fontAwesome)
    // implementation(libs.composeIcons.simpleIcons)
    implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.0")
    implementation("br.com.devsrsouza.compose.icons:simple-icons:1.1.0")

    // Android Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Externe Build-Abhängigkeiten (ApkSigner & AXML Editor)
    // Da diese oft als lokale JARs vorliegen, binden wir das libs-Verzeichnis ein:
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    
    // Test-Abhängigkeiten
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}