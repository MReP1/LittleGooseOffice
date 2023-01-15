package plugin

import Dependencies
import Versions
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            applyAndroid<CommonExtension<*, *, *, *>> {
                configureCompose()
            }
            applyDependencies()
        }
    }

    private fun CommonExtension<*, *, *, *>.configureCompose() {
        buildFeatures {
            compose = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = Versions.compose_compiler
        }
    }

    private fun Project.applyDependencies() {
        dependencies {
            "implementation"(Dependencies.Compose.activity)
            "implementation"(Dependencies.Compose.ui)
            "implementation"(Dependencies.Compose.ui_tool_preview)
            "implementation"(Dependencies.Compose.material3)
            "implementation"(Dependencies.Compose.hilt_navigation)

            "implementation"(Dependencies.Compose.Accompanist.navigation_animation)

            "androidTestImplementation"(Dependencies.Compose.ui_test_junit4)
            "debugImplementation"(Dependencies.Compose.ui_tooling)
            "debugImplementation"(Dependencies.Compose.ui_test_manifest)
        }
    }

}