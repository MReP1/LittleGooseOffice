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
