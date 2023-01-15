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
    }

    private fun BaseAppModuleExtension.configureAndroid() {
        compileSdk = AndroidConfigConventions.COMPILE_SDK_VERSION
        namespace = AndroidConfigConventions.APP_NAMESPACE

        defaultConfig {
            applicationId = AndroidConfigConventions.APPLICATION_ID
            minSdk = AndroidConfigConventions.MIN_SDK_VERSION
            targetSdk = AndroidConfigConventions.TARGET_SDK_VERSION
            versionCode = AndroidConfigConventions.VersionCode
            versionName = AndroidConfigConventions.VersionName

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

        packagingOptions {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    private fun Project.applyDependencies() {
        dependencies {
            implementationDefaultTestDependencies()
        }
    }

}