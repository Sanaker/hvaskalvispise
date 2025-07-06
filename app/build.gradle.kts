// D:\Coding\hvaskalvispise\app\build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // <-- ADDED THIS LINE: The KSP plugin!
    // REMOVED: alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.sanaker.hvaskalvispise"
    compileSdk = 36 // A common stable target SDK

    defaultConfig {
        applicationId = "com.sanaker.hvaskalvispise"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Standard for Android projects
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8" // Matches Java 1.8
    }
    // REMOVED: buildFeatures { compose = true }
    // REMOVED: composeOptions { kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() }
}

dependencies {
    // Core AndroidX Libraries for View System
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)

    // Lifecycle (for View-based apps)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Gson
    implementation(libs.gson)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room Components - CRITICAL CHANGE HERE
    implementation(libs.androidx.room.runtime)
    // annotationProcessor(libs.androidx.room.compiler) // <-- REMOVE THIS LINE
    // kapt(libs.androidx.room.room.compiler) // <-- REMOVE THIS LINE
    ksp(libs.androidx.room.room.compiler) // <-- ADDED/CORRECTED THIS LINE: Use KSP for Room compiler
    implementation(libs.androidx.room.ktx)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // REMOVED ALL COMPOSE-RELATED DEPENDENCIES
    // (e.g., platform(libs.androidx.compose.bom), androidx.ui, androidx.activity.compose, etc.)
}