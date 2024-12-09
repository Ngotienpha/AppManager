plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.onlinestoreapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.onlinestoreapp"
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
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    // Glider
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    //RxJava
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation ("io.reactivex.rxjava3:rxjava:3.0.0")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")

    //bradge
    implementation("com.nex3z:notification-badge:1.0.4")

    //event bus
    implementation("org.greenrobot:eventbus:3.3.1")

    //paper
    implementation ("io.github.pilgr:paperdb:2.7.2")

    //gson
    implementation ("com.google.code.gson:gson:2.11.0")

    //lotte
    implementation ("com.airbnb.android:lottie:6.5.2")

    //neumorphism
    implementation ("com.github.fornewid:neumorphism:0.3.2")

    //image pick
    implementation ("com.github.dhaval2404:imagepicker:2.1")

    //firestore
    implementation ("com.google.firebase:firebase-firestore:25.1.1")

    //chart
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //sliderimage
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

    //videosdk
    implementation ("live.videosdk:rtc-android-sdk:0.1.37")
    // library to perform Network call to generate a meeting id
    implementation ("com.amitshekhar.android:android-networking:1.0.2")
}