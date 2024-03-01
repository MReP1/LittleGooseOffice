package plugin

import AndroidConfigConventions
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Configures the Android specific settings for the application.
 */
internal fun ApplicationExtension.configureAndroidApplication() {
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

/**
 * Configures the Android settings for the library extension.
 */
internal fun LibraryExtension.configureAndroidLibrary() {
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

fun Project.configureKotlin(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    with(commonExtension) {
        compileOptions {
            sourceCompatibility = AndroidConfigConventions.JAVA_VERSION
            targetCompatibility = AndroidConfigConventions.JAVA_VERSION
        }
        kotlinOptions {
            jvmTarget = AndroidConfigConventions.JAVA_VERSION.toString()
            freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
                addAll(
                    listOf(
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
                    )
                )
                // open Kotlin context feature
                add("-Xcontext-receivers")
            }
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            // JVM 17 not support sealed class, so use JVM 11 yet.
            jvmTarget.set(JvmTarget.fromTarget(AndroidConfigConventions.JAVA_VERSION.toString()))
        }
    }

}

fun CommonExtension<*, *, *, *, *, *>.configureAndroidCommon() {
    compileSdk = AndroidConfigConventions.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AndroidConfigConventions.MIN_SDK_VERSION

        vectorDrawables {
            useSupportLibrary = true
        }
    }
}