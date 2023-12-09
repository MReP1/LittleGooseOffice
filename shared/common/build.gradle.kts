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
            //put your multiplatform dependencies here
        }
        commonTest.dependencies {

        }
        androidMain {

        }
        iosMain {

        }
    }
}

android {
    namespace = "little.goose.shared.common"
}
