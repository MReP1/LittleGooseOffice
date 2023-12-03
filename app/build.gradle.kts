plugins {
    alias(libs.plugins.goose.android.application)
    alias(libs.plugins.goose.android.compose)
    alias(libs.plugins.goose.android.hilt)
    alias(libs.plugins.goose.koin)
}

android {
    namespace = "little.goose.office"

    defaultConfig {
        applicationId = AndroidConfigConventions.LittleGoose.APPLICATION_ID
        versionCode = AndroidConfigConventions.LittleGoose.VERSION_CODE
        versionName = AndroidConfigConventions.LittleGoose.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.startUp.runtime)

    implementation(project(":feature:home"))
    implementation(project(":feature:account"))
    implementation(project(":feature:note"))
    implementation(project(":feature:memorial"))
    implementation(project(":feature:search"))

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(project(":core:design-system"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // DataStore
    implementation(libs.androidx.dataStore.preferences)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // lifecycle
    implementation(libs.androidx.lifecycle.rumtime.ktx)
    implementation(libs.androidx.lifecycle.viewModel.ktx)

    implementation(project(":core:common"))
    implementation(project(":feature:settings"))
    implementation(project(":appwidget"))

}