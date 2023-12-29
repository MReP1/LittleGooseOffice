plugins {
    alias(libs.plugins.goose.android.application)
    alias(libs.plugins.goose.android.compose)
}

android {
    namespace = "little.goose.design.catalog"

    defaultConfig {
        applicationId = "little.goose.design.catalog"
        versionCode = 1
        versionName = "1.0.0"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.rumtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.ui.graphics)
    implementation(libs.androidx.compose.navigation)
    implementation(project(":core:design-system"))
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    implementation(project(":shared:chart"))
}