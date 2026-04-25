plugins {
    alias(libs.plugins.crypto.jvm.library)
    alias(libs.plugins.crypto.android.detekt)
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.paging.common)
    implementation(libs.kotlinx.coroutines.core)
    implementation("javax.inject:javax.inject:1")

    testImplementation(libs.junit)
}
