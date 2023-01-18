package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

@Suppress("unused")
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
        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
        dependencies {
            add("implementation", libs.findLibrary("hilt.android").get())
            add("kapt", libs.findLibrary("hilt.compiler").get())
        }
    }

}