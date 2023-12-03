plugins {
    `kotlin-dsl`
}

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = libs.plugins.goose.android.library.get().pluginId
            implementationClass = "plugin.AndroidLibraryConventionPlugin"
        }
        register("androidApp") {
            id = libs.plugins.goose.android.application.get().pluginId
            implementationClass = "plugin.AndroidAppConventionPlugin"
        }
        register("androidHilt") {
            id = libs.plugins.goose.android.hilt.get().pluginId
            implementationClass = "plugin.HiltConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.goose.android.compose.get().pluginId
            implementationClass = "plugin.ComposeConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.goose.android.room.get().pluginId
            implementationClass = "plugin.RoomConventionPlugin"
        }
        register("koin") {
            id = libs.plugins.goose.koin.get().pluginId
            implementationClass = "plugin.KoinConventionPlugin"
        }
        register("kotlinMultiplatform") {
            id = libs.plugins.goose.kotlin.multiplatform.get().pluginId
            implementationClass = "plugin.KotlinMultiplatformConventionPlugin"
        }
    }
}