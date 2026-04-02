plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.snapvault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.snapvault"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Core AndroidX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.fragment:fragment:1.8.5")

    // Material Design 3
    implementation("com.google.android.material:material:1.12.0")

    // CameraX — modern camera API
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // Glide — fast image loading for gallery grid
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Activity Result API
    implementation("androidx.activity:activity:1.9.3")
}