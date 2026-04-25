plugins {
    alias(libs.plugins.crypto.android.library)
    alias(libs.plugins.crypto.android.detekt)
}

android {
    namespace = "com.abhay.crypto.core.testing"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines.core)
}
