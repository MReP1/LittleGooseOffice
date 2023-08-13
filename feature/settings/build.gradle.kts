plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
}

android {
    namespace = "little.goose.settings"
}

dependencies {
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(project(":core:design-system"))
}