plugins {
    alias(libs.plugins.goose.android.library)
    alias(libs.plugins.goose.android.compose)
}

android {
    namespace = "little.goose.ui"
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:design-system"))
}