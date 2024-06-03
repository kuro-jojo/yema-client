// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.3.15" apply false
    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "2.9.9" apply false

}
buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVersion = "2.5.3"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}