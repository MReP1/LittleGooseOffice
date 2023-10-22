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
            id = "goose.android.library"
            implementationClass = "plugin.AndroidLibraryConventionPlugin"
        }
        register("androidApp") {
            id = "goose.android.app"
            implementationClass = "plugin.AndroidAppConventionPlugin"
        }
        register("androidHilt") {
            id = "goose.android.hilt"
            implementationClass = "plugin.HiltConventionPlugin"
        }
        register("androidCompose") {
            id = "goose.android.compose"
            implementationClass = "plugin.ComposeConventionPlugin"
        }
        register("androidRoom") {
            id = "goose.android.room"
            implementationClass = "plugin.RoomConventionPlugin"
        }
        register("androidKoin") {
            id = "goose.android.koin"
            implementationClass = "plugin.KoinConventionPlugin"
        }
    }
}