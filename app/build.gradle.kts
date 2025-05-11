// This is your app/build.gradle.kts (for module app)

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp") version "1.9.22-1.0.16"
}

android {

    buildFeatures {
        buildConfig = true
        compose = true
    }

    namespace = "com.example.pawpin_v2"
    compileSdk = 35

    defaultConfig {

        buildConfigField("String", "MAPS_API_KEY", "\"${project.properties["MAPS_API_KEY"]}\"")

        applicationId = "com.example.pawpin_v2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🚀 Room schema export fix (required by kapt processor)
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas".toString(),
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10" // matching Compose BOM
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.navigation:navigation-compose:2.8.2")
    implementation("com.google.maps.android:maps-compose:6.4.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Room CORRECT way:
//    implementation("androidx.room:room-runtime:2.5.2") // <-- Add this line (room-runtime)
//    implementation("androidx.room:room-ktx:2.5.2")      // <-- You already have
//    ksp("androidx.room:room-compiler:2.5.2")

    implementation ("androidx.room:room-runtime:2.5.0")
    implementation ("androidx.room:room-ktx:2.5.0")
    ksp("androidx.room:room-compiler:2.5.0")
    implementation ("androidx.room:room-testing:2.5.1")

    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha")
    implementation("androidx.compose.runtime:runtime-livedata")

    implementation ("com.maxkeppeler.sheets-compose-dialogs:calendar:1.0.0")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.json:json:20231013")
    implementation ("com.google.accompanist:accompanist-flowlayout:0.30.1")

    implementation ("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
