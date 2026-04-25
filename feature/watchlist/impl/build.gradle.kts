plugins {
    alias(libs.plugins.crypto.android.feature.impl)
    alias(libs.plugins.crypto.android.detekt)
}

android {
    namespace = "com.abhay.crypto.feature.watchlist"
}

dependencies {
    implementation(project(":feature:watchlist:api"))
    implementation(project(":feature:widget:api"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    implementation(libs.glance.appwidget)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.turbine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
