plugins {
    id("goose.android.app")
    id("goose.android.compose")
    id("goose.android.hilt")
    alias(libs.plugins.ksp)
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
    implementation(project(":feature:schedule"))
    implementation(project(":feature:search"))

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(project(":core:design-system"))
    implementation(project(":core:ui"))
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    implementation("androidx.compose.animation:animation-graphics")

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)

    // DataStore
    implementation(libs.androidx.dataStore.preferences)

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // lifecycle
    implementation(libs.androidx.lifecycle.rumtime.ktx)
    implementation(libs.androidx.lifecycle.viewModel.ktx)

    implementation(project(":core:common"))

}