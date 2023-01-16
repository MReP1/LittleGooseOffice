package plugin

import AndroidConfigConventions
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.dependencies

class AndroidAppConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            pluginManager.applyPlugin()
            applyAndroid<BaseAppModuleExtension> {
                configureAndroid()
                configureKotlin()
            }
            applyDependencies()
        }
    }

    private fun PluginManager.applyPlugin() {
        apply("com.android.application")
        apply("org.jetbrains.kotlin.android")
        apply("kotlin-kapt")
        apply("kotlin-parcelize")
        apply("com.google.devtools.ksp")
    }

    private fun BaseAppModuleExtension.configureAndroid() {
        configureAndroidCommon()

        defaultConfig.targetSdk = AndroidConfigConventions.TARGET_SDK_VERSION

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
    }

    private fun Project.applyDependencies() {
        dependencies {
            implementationDefaultTestDependencies()
        }
    }

}