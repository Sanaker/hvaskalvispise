// D:\Coding\hvaskalvispise\settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // CRITICAL: Explicitly add JetBrains Space Maven for Kotlin plugins
        maven { url = uri("https://maven.pkg.jetbrains.space/public/libraries/maven") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add this again here for general dependencies if needed, though pluginManagement is key
        maven { url = uri("https://maven.pkg.jetbrains.space/public/libraries/maven") }
    }
}
rootProject.name = "hvaskalvispise"
include(":app")