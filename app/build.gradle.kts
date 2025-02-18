plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "uz.isds.meterdetection"
    compileSdk = 35

    defaultConfig {
        applicationId = "uz.isds.meterdetection"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    compileOnly(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(project(":meterAI"))
    androidTestImplementation(platform(libs.androidx.compose.bom))
//    implementation("com.github.zokirjonkarimov:meter-detection:1.1.3")
//    implementation("com.github.zokirjonkarimov:meter-detection:1.1.9")
}

