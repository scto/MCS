pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MCS"
include(":app")
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:editor")
include(":core:navigation")
include(":core:resourcess")
include(":core:terminal")
include(":core:ui")
include(":core:utils")
include(":feature:onboarding")
include(":feature:setup")
include(":feature:dashboard")
include(":feature:editor")
include(":feature:settings")
include(":feature:debug")
