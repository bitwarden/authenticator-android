// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlinx.kover) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    id("org.sonarqube") version "5.0.0.4638"
}

sonar {
    properties {
        property("sonar.projectKey", "bitwarden_authenticator-android")
        property("sonar.organization", "bitwarden")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "app/src/main/,app/src/debug/")
        property("sonar.tests", "app/src/test/,app/src/androidTest/")
    }
}

