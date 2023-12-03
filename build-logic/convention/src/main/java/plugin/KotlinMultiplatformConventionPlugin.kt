package plugin

import AndroidConfigConventions
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply(libs.findPlugin("android-library").get().get().pluginId)
                apply(libs.findPlugin("kotlin-multiplatform").get().get().pluginId)
            }

            extensions.configure<KotlinMultiplatformExtension> {
                androidTarget()
                jvmToolchain(AndroidConfigConventions.JAVA_VERSION.toString().toInt())
            }

            applyAndroid<LibraryExtension> {
                configureAndroidLibrary()

                compileOptions {
                    sourceCompatibility = AndroidConfigConventions.JAVA_VERSION
                    targetCompatibility = AndroidConfigConventions.JAVA_VERSION
                }

                tasks.withType<KotlinCompile>().configureEach {
                    compilerOptions {
                        // JVM 17 not support sealed class, so use JVM 11 yet.
                        jvmTarget.set(JvmTarget.fromTarget(AndroidConfigConventions.JAVA_VERSION.toString()))
                    }
                }
            }
        }
    }
}