package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpComposePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply(libs.findPlugin("jetbrains-compose").get().get().pluginId)
            }
            applyComposeStrongSkippingMode()

            extensions.configure<KotlinMultiplatformExtension> {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                        "-opt-in=org.jetbrains.compose.resources.ExperimentalResourceApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
                    )
                }
            }
        }
    }

}