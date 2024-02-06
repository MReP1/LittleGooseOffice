plugins {
    alias(libs.plugins.goose.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.goose.android.compose)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FeatureNote"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            compileOnly(libs.koin.core)
            implementation(libs.compose.markdown)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(project(":shared:common"))
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.compose.ui.tool.preview)
        }
        iosMain.dependencies {
        }
    }

}

android {
    namespace = "little.goose.feature.note"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    dependencies {
        debugImplementation(libs.androidx.compose.ui.tooling)
    }
}

tasks.register("testClasses") {

}