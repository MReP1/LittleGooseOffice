package plugin

import AndroidConfigConventions
import com.android.build.api.dsl.CommonExtension

fun CommonExtension<*, *, *, *>.configureKotlin() {

    compileOptions {
        sourceCompatibility = AndroidConfigConventions.JAVA_VERSION
        targetCompatibility = AndroidConfigConventions.JAVA_VERSION
    }

    kotlinOptions {
        jvmTarget = AndroidConfigConventions.JAVA_VERSION.toString()
        freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
            add("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
            add("-opt-in=androidx.compose.animation.ExperimentalAnimationApi")
        }
    }

}

fun CommonExtension<*, *, *, *>.configureAndroidCommon() {
    compileSdk = AndroidConfigConventions.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AndroidConfigConventions.MIN_SDK_VERSION

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}