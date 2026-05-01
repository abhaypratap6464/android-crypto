import com.abhay.crypto.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            dependencies {
                "ksp"(libs.findLibrary("hilt-compiler").get())
            }

            // For non-Android modules (like :core:domain)
            pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                dependencies {
                    "implementation"(libs.findLibrary("hilt-core").get())
                }
            }

            // For Android modules
            pluginManager.withPlugin("com.android.base") {
                apply(plugin = "com.google.dagger.hilt.android")
                dependencies {
                    "implementation"(libs.findLibrary("hilt-android").get())
                }
            }
        }
    }
}
