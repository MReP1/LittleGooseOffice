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
            baseName = "design"
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
                implementation(project(":shared:common"))
            }
        }
        androidMain {

        }
    }
}

android {
    namespace = "little.goose.design"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}
