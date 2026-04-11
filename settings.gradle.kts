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
project(":app").projectDir = file("app")

include(":core:data")
project(":core:data").projectDir = file("core/data")

include(":core:domain")
project(":core:domain").projectDir = file("core/domain")

include(":core:editor")
project(":core:editor").projectDir = file("core/editor")

include(":core:navigation")
project(":core:navigation").projectDir = file("core/navigation")

include(":core:resourcess")
project(":core:resourcess").projectDir = file("core/resourcess")

include(":core:terminal")
project(":core:terminal").projectDir = file("core/terminal")

include(":core:ui")
project(":core:ui").projectDir = file("core/ui")

include(":core:utils")
project(":core:utils").projectDir = file("core/utils")

include(":feature:onboarding")
project(":feature:onboarding").projectDir = file("feature/onboarding")

include(":feature:setup")
project(":feature:setup").projectDir = file("feature/setup")

include(":feature:dashboard")
project(":feature:dashboard").projectDir = file("feature/dashboard")

include(":feature:editor")
project(":feature:editor").projectDir = file("feature/editor")

include(":feature:settings")
project(":feature:settings").projectDir = file("feature/settings")

include(":feature:debug")
project(":feature:debug").projectDir = file("feature/debug")
