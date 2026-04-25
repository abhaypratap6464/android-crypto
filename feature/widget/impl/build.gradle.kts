plugins {
    alias(libs.plugins.crypto.android.feature.impl)
    alias(libs.plugins.crypto.android.detekt)
}

android {
    namespace = "com.abhay.crypto.feature.widget"
}

dependencies {
    implementation(project(":feature:widget:api"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.work.runtime)
}
