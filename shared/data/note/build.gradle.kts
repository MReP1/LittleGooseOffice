plugins {
    alias(libs.plugins.goose.kotlin.multiplatform)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("GooseNoteDatabase") {
            packageName.set("little.goose.note")
        }
    }
    linkSqlite = true
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "DataNote"
            isStatic = false
        }
    }

    sourceSets {
        commonMain.dependencies {
            compileOnly(libs.koin.core)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(project(":shared:common"))
            api(project(":shared:data:database"))
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }

}

android {
    namespace = "little.goose.data.note"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}