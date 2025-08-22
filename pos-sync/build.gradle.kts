plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.ksp)
    alias(libs.plugins.hilt.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.desapabandara.pos.sync"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += listOf("environment")

    productFlavors {
        create("dev") {
            dimension = "environment"
        }

        create("live") {
            dimension = "environment"
        }
    }
}

dependencies {
    implementation(project(":android-base"))
    implementation(project(":pos-local-db"))
    implementation(project(":pos-preference"))
    implementation(project(":pos-base"))
    implementation(project(":pos-backend"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.gson)
    implementation(libs.timber)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}