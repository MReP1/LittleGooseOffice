plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
    alias(libs.plugins.goose.android.hilt)
}

android {
    namespace = "little.goose.settings"
}

dependencies {
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(project(":core:design-system"))
}