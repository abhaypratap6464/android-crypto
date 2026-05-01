plugins {
    alias(libs.plugins.crypto.jvm.library)
    alias(libs.plugins.crypto.hilt)
    alias(libs.plugins.crypto.android.detekt)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
}
