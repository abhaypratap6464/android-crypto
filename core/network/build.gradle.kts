plugins {
    alias(libs.plugins.crypto.android.library)
    alias(libs.plugins.crypto.hilt)
    alias(libs.plugins.crypto.android.detekt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.abhay.crypto.core.network"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx)
    implementation(libs.kotlinx.serialization.json)
}
