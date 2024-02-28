plugins {
    alias(libs.plugins.goose.kotlin.multiplatform)
    alias(libs.plugins.goose.compose.multiplatform)
}

kotlin {

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared.ui"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(project(":shared:common"))
                implementation(libs.koin.compose)
                compileOnly(libs.koin.core)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.koin.android)
            }
        }
        iosMain {
            dependencies {

            }
        }
    }
}

android {
    namespace = "little.goose.shared.ui"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

}
