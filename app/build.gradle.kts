import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

val keystorePropertiesFile = rootProject.file("key/keystore.properties")
val keystoreProperties = Properties()

// Load properties from key.properties file
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

private val _storeFile       = keystoreProperties.getProperty("storeFile")
private val _storePassword   = keystoreProperties.getProperty("storePassword")
private val _keyAlias        = keystoreProperties.getProperty("keyAlias")
private val _keyPassword     = keystoreProperties.getProperty("keyPassword")

android {
    namespace = "com.example.smartgarden"
    compileSdk = 34

    signingConfigs {
        create("release") {
            this.storeFile       = File(_storeFile)
            this.storePassword   = _storePassword

            this.keyAlias        = _keyAlias
            this.keyPassword     = _keyPassword
        }
    }

    defaultConfig {
        applicationId = "com.example.smartgarden"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-storage")

    //Dagger - Hilt + navigation hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Material 3 - design library
    implementation("androidx.compose.material3:material3:1.1.2")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Util
    implementation("com.google.code.gson:gson:2.10")

    // ssh library
    implementation("com.jcraft:jsch:0.1.55")

    // Camera and qr import
    implementation ("com.google.mlkit:barcode-scanning:17.2.0") // Check for the latest version
    implementation ("androidx.camera:camera-camera2:1.3.1") // CameraX dependencies
    implementation ("androidx.camera:camera-lifecycle:1.3.1")
    implementation ("androidx.camera:camera-view:1.3.1")

    // Live data
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")

    // Url image
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Video url image
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}