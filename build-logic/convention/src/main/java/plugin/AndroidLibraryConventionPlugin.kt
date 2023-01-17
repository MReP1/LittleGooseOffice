package plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            pluginManager.applyPlugin()
            applyAndroid<LibraryExtension> {
                configureKotlin()
                configureAndroid()
            }
            applyDependencies()
        }
    }

    private fun PluginManager.applyPlugin() {
        apply("com.android.library")
        apply("org.jetbrains.kotlin.android")
    }

    private fun LibraryExtension.configureAndroid() {
        configureAndroidCommon()

        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }

    private fun Project.applyDependencies() {
        dependencies {
            implementationDefaultTestDependencies()
        }
    }
}