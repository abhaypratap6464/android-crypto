plugins {
    alias(libs.plugins.crypto.android.library)
    alias(libs.plugins.crypto.hilt)
    alias(libs.plugins.crypto.android.detekt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.abhay.crypto.core.data"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:network"))

    implementation(libs.retrofit)
    implementation(libs.paging.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.turbine)
}
