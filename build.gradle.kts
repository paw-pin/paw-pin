// This is your root build.gradle.kts
// No versions hardcoded here, centralized or pulled by plugins

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    //id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
}

buildscript {
    dependencies {
        // Optional: If you use Google services like Firebase or Maps (for Maps API loading)
        classpath("com.google.gms:google-services:4.4.1")
    }
}
