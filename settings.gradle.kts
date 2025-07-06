// D:\Coding\hvaskalvispise\settings.gradle.kts

pluginManagement {
    repositories {
        // These are the repositories where Gradle looks for the plugins themselves!
        google()       // <--- ESSENTIAL FOR KSP AND OTHER GOOGLE PLUGINS
        mavenCentral()
        gradlePluginPortal() // Generally good to have for most plugins
        maven("https://maven.pkg.jetbrains.space/public/libraries/maven") // For JetBrains-related artifacts
    }
    // The `plugins {}` block can also be here for centralizing versions,
    // but the Android docs suggest it in root build.gradle.kts.
    // We'll follow the docs for now if you prefer.
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // These are for regular dependencies (libraries)
        google()
        mavenCentral()
    }
}

rootProject.name = "hvaskalvispise"
include(":app")