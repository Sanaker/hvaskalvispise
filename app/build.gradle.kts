// D:\Coding\hvaskalvispise\app\build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // This is where it's applied to the app module
}

android {
    namespace = "com.sanaker.hvaskalvispise"
    compileSdk = 34 // Use 34, a stable target for now

    defaultConfig {
        applicationId = "com.sanaker.hvaskalvispise"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true // CRITICAL: Enable Compose features
    }
    composeOptions {
        // CRITICAL: Get Compose Compiler version from libs.versions.toml
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Core AndroidX Libraries for basic app setup
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    // Removed constraintlayout and recyclerview if not explicitly used with Views

    // Lifecycle for Compose (ViewModel, LiveData)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Activity Compose (for setting up Compose content in MainActivity)
    implementation(libs.androidx.activity.compose)

    // Compose BOM (Bill of Materials) for consistent Compose versions
    implementation(platform(libs.androidx.compose.bom))

    // Specific Compose libraries (versions managed by the BOM)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Gson (keep if you need it for data handling)
    implementation(libs.gson)

    // For @Preview annotations in Android Studio (debug build only)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // For unit tests
    testImplementation(libs.junit)

    // For Android instrumented tests (Compose UI testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Same BOM version for tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
}