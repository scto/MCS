Act as an Android Architect. I want to fully replicate the functionality of the "AppIntro" library within the module :core:onboarding (namespace com.scto.mcs) using only Jetpack Compose.
Current Context:
* Project: MCS
* Language: Kotlin
* UI: Jetpack Compose + Material 3
* Dependencies: Hilt, Coil, Pager Foundation (configured in gradle/libs.versions.toml)
Tasks:

1. Create core/onboarding/src/main/java/com/scto/mcs/core/onboarding/state/OnboardingViewModel.kt:

   * Implement thread-safe dynamic slide management (MutableStateFlow).

   * Add a vibrate() helper using VibratorManager.

2. Create core/onboarding/src/main/java/com/scto/mcs/core/onboarding/ui/OnboardingScreen.kt:

   * Use HorizontalPager from androidx.compose.foundation.pager.

   * Implement animateColorAsState for background transitions.

   * Add a custom OnboardingBottomBar with animated dot indicators.

3. Add support for "Permission Slides":

   * The slide should only allow "Next" after the specified Android permission is granted (use rememberLauncherForActivityResult).

4. Ensure all code follows Clean Architecture and uses the com.scto.mcs hierarchy correctly.