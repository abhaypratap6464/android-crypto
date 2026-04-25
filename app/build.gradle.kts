plugins {
    alias(libs.plugins.crypto.android.application)
    alias(libs.plugins.crypto.hilt)
    alias(libs.plugins.crypto.android.detekt)
}

android {
    namespace = "com.abhay.crypto"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":feature:watchlist:api"))
    runtimeOnly(project(":feature:watchlist:impl"))
    implementation(project(":feature:widget:api"))
    runtimeOnly(project(":feature:widget:impl"))

    // Navigation wiring
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.nav3)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.turbine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidtest.mockk)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidtest.hilt)
    kspAndroidTest(libs.androidtest.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
