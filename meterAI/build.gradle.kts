plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "uz.isds.meterai"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val cameraxVersion = "1.4.0-alpha04"
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")

    implementation("org.tensorflow:tensorflow-lite:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    implementation("org.tensorflow:tensorflow-lite-gpu-delegate-plugin:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-gpu-api:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-api:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.16.1")
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:2.16.1")
}

//publishing {
//    publications {
//        register<MavenPublication>("release") {
//            afterEvaluate {
//                from(components["release"])
//                groupId = "uz.isds"
//                artifactId = "meter"
//                version = "1.0"
//            }
//        }
//    }
//}

//repositories {
//    maven {
//        name = "GitLab"
//        url = uri("https://gitlab.isds.uz/api/v4/projects/machine-learning%2Fdevice%2Fsdk%2Fandroid%2Fmeter-detection/packages/maven")
//        credentials {
//            username = project.findProperty("gitlabUsername") ?: System.getenv("CI_JOB_TOKEN")
//            password = project.findProperty("gitlabToken") ?: System.getenv("CI_JOB_TOKEN")
//        }
//    }
//}
repositories{
    name
    uri("https://github.com/Zokirjon23/meter-detection")
}

afterEvaluate {
    android.libraryVariants.onEach { variant ->
        publishing.publications.create(variant.name,MavenPublication::class.java){
            from(components.findByName(variant.name))

            groupId = "uz.isds"
            artifactId = "meter-detection"
            version = "1.0.0"
        }
    }
}