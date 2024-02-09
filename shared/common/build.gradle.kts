plugins {
    alias(libs.plugins.goose.kotlin.multiplatform)
}

kotlin {

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "common"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {

        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }
        iosMain.dependencies {

        }
    }
}

android {
    namespace = "little.goose.shared.common"
}
