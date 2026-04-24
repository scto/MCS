pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://cache-redirector.jetbrains.com/kotlin.bintray.com/kotlin-plugin") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
            // Wir verbieten JitPack, Pakete der Sora-Editor Gruppe zu bedienen
            content {
                excludeGroup("io.github.rosemoe.sora")
            }
        }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}


rootProject.name = "MCS"

include(":app")

// Core modules
include(
    ":core:build-tools",
    ":core:commands",
    ":core:domain",
    ":core:files",
    ":core:git",
    ":core:navigation",
    ":core:resourcess",
    ":core:terminal",
    ":core:terminal-emulator",
    ":core:termux-shared",
    ":core:ui"
)

// Feature modules
include(
    ":feature:editor",
    ":feature:settings"
)

// Termux modules
include(
    ":termux:application",
    ":termux:emulator",
    ":termux:shared",
    ":termux:view"
)
