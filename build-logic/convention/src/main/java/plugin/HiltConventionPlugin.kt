package plugin

import Dependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            pluginManager.applyPlugin()
            applyKapt()
            applyDependencies()
        }
    }

    private fun PluginManager.applyPlugin() {
        apply("org.jetbrains.kotlin.kapt")
        apply("dagger.hilt.android.plugin")
    }

    private fun Project.applyKapt() {
        applyKapt {
            correctErrorTypes = true
        }
    }

    private fun Project.applyDependencies() {
        dependencies {
            "implementation"(Dependencies.Hilt.hilt_android)
            "kapt"(Dependencies.Hilt.hilt_android_compiler)
        }
    }

}