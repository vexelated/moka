plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.capstoneapps.moka"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.capstoneapps.moka"
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
        viewBinding = true
        mlModelBinding = true
    }
}

dependencies {
    implementation("com.google.mlkit:vision-common:17.3.0")
    implementation("com.google.android.gms:play-services-mlkit-face-detection:17.1.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.databinding:databinding-runtime:8.2.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0")

    // Camera and Image Processing
    val cameraxVersion = "1.2.3"
    //noinspection GradleDependency
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    //noinspection GradleDependency
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.13.0")
    implementation("org.tensorflow:tensorflow-lite-gpu-delegate-plugin:0.4.4")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")
    implementation("org.tensorflow:tensorflow-lite:2.13.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")

    // UI and Views
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Glide - Image Loading Library
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1") // Glide Annotation Processor

    // DataStore - AndroidX DataStore for Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // AndroidX Lifecycle Libraries
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // ViewModel with Kotlin Extensions
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2") // LiveData with Kotlin Extensions
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2") // ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2") // Lifecycle Runtime

    // Retrofit - HTTP Client
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON Converter
    implementation("com.google.code.gson:gson:2.8.9") // JSON Converter
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // OkHttp Logging Interceptor
}