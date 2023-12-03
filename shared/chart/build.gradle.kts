plugins {
    alias(libs.plugins.goose.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
            }
        }
        val androidMain by getting {

        }
    }
}

android {
    namespace = "little.goose.chart"
}
