plugins {
    alias(libs.plugins.crypto.android.feature.api)
    alias(libs.plugins.crypto.android.detekt)
}

android {
    namespace = "com.abhay.crypto.feature.watchlist.api"
}

dependencies {
    implementation(libs.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)
}
