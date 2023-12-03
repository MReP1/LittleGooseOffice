package plugin

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class AndroidAppConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {

            with(pluginManager) {
                apply(libs.findPlugin("android-application").get().get().pluginId)
                apply(libs.findPlugin("kotlin-android").get().get().pluginId)
            }

            extensions.configure<ApplicationExtension> {
                configureAndroidApplication()
                configureKotlin(this)
            }

            dependencies {
                implementationDefaultTestDependencies(libs)
            }

        }
    }

}