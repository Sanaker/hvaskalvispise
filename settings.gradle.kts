// D:\Coding\hvaskalvispise\settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // Keep this explicitly, as other Kotlin plugins/libraries might still need it
        maven { url = uri("https://maven.pkg.jetbrains.space/public/libraries/maven") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Keep this explicitly, as other Kotlin libraries might still need it
        maven { url = uri("https://maven.pkg.jetbrains.space/public/libraries/maven") }
    }
}

rootProject.name = "hvaskalvispise"
include(":app")