package plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {

            with(pluginManager) {
                apply(libs.findPlugin("android-library").get().get().pluginId)
                apply(libs.findPlugin("kotlin-android").get().get().pluginId)
            }

            applyAndroid<LibraryExtension> {
                configureKotlin(this)
                configureAndroidLibrary()
            }

            dependencies {
                implementationDefaultTestDependencies(libs)
            }

        }
    }
}