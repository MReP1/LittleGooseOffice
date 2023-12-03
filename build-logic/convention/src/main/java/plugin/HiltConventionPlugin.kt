package plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class HiltConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply(libs.findPlugin("hilt").get().get().pluginId)
                apply(libs.findPlugin("ksp").get().get().pluginId)
            }
            applyDependencies()
        }
    }

    private fun Project.applyDependencies() {
        dependencies {
            add("implementation", libs.findLibrary("hilt.android").get())
            add("ksp", libs.findLibrary("hilt.compiler").get())
        }
    }

}