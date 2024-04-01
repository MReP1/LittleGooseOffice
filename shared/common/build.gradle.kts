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
    ).forEach {
        it.binaries.framework {
            baseName = "common"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)

            implementation(compose.runtime)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewModel)
            implementation(libs.androidx.lifecycle.runtime)
            implementation(libs.androidx.lifecycle.common)
        }
        commonTest.dependencies {

        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.lifecycle.viewModel.ktx)
            implementation(libs.androidx.lifecycle.viewModel.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.kotlinx.coroutines.android)
        }
        iosMain.dependencies {

        }
    }
}

android {
    namespace = "little.goose.shared.common"
}
