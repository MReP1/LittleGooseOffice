@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("goose.android.app")
    id("goose.android.compose")
    alias(libs.plugins.ksp)
}

android {
    namespace = "little.goose.account"

    defaultConfig {
        applicationId = AndroidConfigConventions.LittleGoose.APPLICATION_ID
        versionCode = AndroidConfigConventions.LittleGoose.VERSION_CODE
        versionName = AndroidConfigConventions.LittleGoose.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.google.android.material)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.recyclerView)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

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

    //MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //富文本
    implementation(project(":RichText"))
    //自定义Calendar
    implementation(project(":calendarview"))

}