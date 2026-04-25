plugins {
    alias(libs.plugins.crypto.android.library)
    alias(libs.plugins.crypto.android.library.compose)
    alias(libs.plugins.crypto.android.detekt)
}

android {
    namespace = "com.abhay.crypto.core.ui"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
