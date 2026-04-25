import com.abhay.crypto.configureAndroidCompose
import com.abhay.crypto.configureKotlinAndroid
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
            apply(plugin = "jacoco")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                namespace = "com.abhay.crypto"
                defaultConfig {
                    applicationId = "com.abhay.crypto"
                    targetSdk = 36
                    versionCode = 1
                    versionName = "1.0"
                    testInstrumentationRunner = "com.abhay.crypto.HiltTestRunner"
                }
                buildTypes {
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
                buildFeatures { buildConfig = true }
                testOptions { unitTests.isReturnDefaultValues = true }
            }
        }
    }
}
