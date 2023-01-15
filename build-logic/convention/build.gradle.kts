plugins {
    `kotlin-dsl`
}

dependencies {
    val kotlinGradlePluginVersion = "1.7.20"
    val androidGradlePluginVersion = "7.4.0"
    implementation("com.android.tools.build:gradle:$androidGradlePluginVersion")
    implementation(kotlin("gradle-plugin", version = kotlinGradlePluginVersion))
    implementation(kotlin("serialization", version = kotlinGradlePluginVersion))

    val hiltGradleVersion = "2.44.2"
    implementation("com.google.dagger:hilt-android-gradle-plugin:$hiltGradleVersion")
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
    }
}