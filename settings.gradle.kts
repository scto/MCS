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

include(":core:navigation")
include(":core:resourcess")
include(":core:ui")

include(":feature:editor")
include(":feature:settings")