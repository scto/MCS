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
    ":core:buildtools",
    ":core:commands",
    ":core:crashhandler",
    ":core:di",
    ":core:domain",
    ":core:editor",
    ":core:exec",
    ":core:extensions",
    ":core:files",
    ":core:filetree",
    ":core:git",
    ":core:navigation",
    ":core:network",
    ":core:resources",
    ":core:runner",
    ":core:template:api",
    ":core:template:data",
    ":core:terminal",
    ":core:terminal-emulator",
    ":core:terminal-view",
    ":core:termux-shared",
    ":core:ui",
    ":core:utils"
)

// Feature modules
include(
    ":feature:editor",
    ":feature:git",
    ":feature:onboarding",
    ":feature:settings",
    ":feature:terminal"
)

val soraX = file("soraX")

if (!soraX.exists() || soraX.listFiles()?.isEmpty() != false) {
    throw GradleException(
        """
        The 'soraX' submodule is missing or empty.

        Please run:
            git submodule update --init --recursive
        """
            .trimIndent()
    )
}

include(
    ":editor",
    ":oniguruma-native",
    ":editor-lsp",
    ":language-textmate"
)

project(":editor").projectDir = file("soraX/editor")
project(":oniguruma-native").projectDir = file("soraX/oniguruma-native")
project(":editor-lsp").projectDir = file("soraX/editor-lsp")
project(":language-textmate").projectDir = file("soraX/language-textmate")
