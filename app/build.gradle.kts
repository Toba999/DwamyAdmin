import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebase.crashlitycs)
    alias(libs.plugins.kotlin.serialization)
}


android {
    namespace = "com.dev.dwamyadmin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dev.dwamyadmin"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //Constraint Layout
    implementation(libs.androidx.constraintlayout)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // Lifecycles only (without ViewModel or LiveData)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    //Hilt dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.hilt.common)
    implementation(libs.hilt.navigation.fragment)



    //Navigation component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)


    // AndroidX Test - JVM testing
    testImplementation(libs.androidx.core.ktx)
    testImplementation(libs.androidx.junit)



    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation (libs.androidx.biometric)
    implementation (libs.play.services.maps)
    implementation (libs.play.services.location)
    //implementation (libs.horizontalcalendar)
    implementation (libs.firebase.storage)
    //shimmer
    implementation(libs.shimmer)



    //coil (Image loading for Android backed by Kotlin Coroutines)
    implementation(libs.coil)
}