// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // Keep this for compose plugin
    alias(libs.plugins.kotlin.kapt) apply false
}

// Define feature modules here if they are not dynamically included
// If you are dynamically including modules in settings.gradle.kts, this might not be necessary.
// However, it's good practice to keep module definitions consistent.
// Example:
// include(":feature:dashboard")
// include(":feature:editor")
// include(":feature:settings") // Now handled in settings.gradle.kts
// include(":feature:onboarding")
// include(":feature:setup")
// include(":feature:debug") // Assuming debug is also a feature module
