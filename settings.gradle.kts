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

val modules = listOf(
    ":app",
    
    ":core:build",
    ":core:data",
    ":core:domain",
    ":core:editor",
    ":core:files",
    ":core:lsp",
    ":core:navigation",
    ":core:resourcess",
    ":core:terminal",
    ":core:ui",
    ":core:utils",
    
    ":feature:onboarding",
    ":feature:setup",
    ":feature:dashboard",
    ":feature:editor",
    ":feature:settings",
    ":feature:debug"
)

modules.forEach { module ->
    include(module)
    val path = module.substring(1).replace(":", "/")
    project(module).projectDir = file(path)
}
include(":feature:projects")
