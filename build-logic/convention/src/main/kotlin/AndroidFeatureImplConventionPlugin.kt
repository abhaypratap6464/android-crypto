import com.abhay.crypto.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureImplConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "crypto.android.library")
            apply(plugin = "crypto.android.library.compose")
            apply(plugin = "crypto.hilt")
            dependencies {
                "implementation"(libs.findLibrary("navigation3-runtime").get())
                "implementation"(libs.findLibrary("navigation3-ui").get())
                "implementation"(libs.findLibrary("lifecycle-viewmodel-nav3").get())
                "implementation"(libs.findLibrary("hilt-navigation-compose").get())
                "implementation"(libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                "implementation"(libs.findLibrary("paging-compose").get())
            }
        }
    }
}
