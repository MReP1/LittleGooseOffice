plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
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